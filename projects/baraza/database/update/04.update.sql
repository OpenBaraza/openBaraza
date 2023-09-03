DROP TABLE sys_translations;
DROP TABLE sys_apps;

CREATE TABLE sys_apps (
	sys_app_id				serial primary key,
	sys_app_name			varchar(50) not null unique,
	sys_app_code			varchar(16) not null unique,
	sys_app_group			varchar(16),
	is_active				boolean default true not null,
	details					text
);

CREATE TABLE sys_app_modules (
	sys_app_module_id		serial primary key,
	sys_app_id				integer references sys_apps,
	sys_app_module_name		varchar(50) not null,
	is_default				boolean default true not null,
	price					real default 0 not null,
	details					text
);
CREATE INDEX sys_app_modules_sys_app_id ON sys_app_modules (sys_app_id);

CREATE TABLE org_apps (
	org_app_id				serial primary key,
	sys_app_id				integer references sys_apps,
	org_id					integer references orgs,
	price					real default 0 not null,
	user_accounts			integer default 1 not null,
	is_montly_bill			boolean default true not null,
	is_annual_bill			boolean default false not null,
	is_active				boolean default true not null,
	created					timestamp default current_timestamp not null,
	details					text,
	UNIQUE(sys_app_id, org_id)
);
CREATE INDEX org_apps_sys_app_id ON org_apps (sys_app_id);
CREATE INDEX org_apps_org_id ON org_apps (org_id);

CREATE TABLE org_app_modules (
	org_app_module_id		serial primary key,
	sys_app_module_id		integer references sys_app_modules,
	org_id					integer references orgs,
	price					real default 0 not null,
	user_accounts			integer default 1 not null,
	is_active				boolean default true not null,
	created					timestamp default current_timestamp not null,
	details					text,
	UNIQUE(sys_app_module_id, org_id)
);
CREATE INDEX org_app_modules_sys_app_module_id ON org_app_modules (sys_app_module_id);
CREATE INDEX org_app_modules_org_id ON org_app_modules (org_id);

CREATE TABLE sys_translations (
	sys_translation_id		serial primary key,
	sys_app_id				integer references sys_apps,
	sys_language_id			integer references sys_languages,
	org_id					integer references orgs,
	reference				varchar(64) not null,
	title					varchar(320) not null,
	narration				varchar(320) not null,

	UNIQUE(sys_app_id, sys_language_id, org_id, reference)
);
CREATE INDEX sys_translations_sys_app_id ON sys_translations (sys_app_id);
CREATE INDEX sys_translations_sys_language_id ON sys_translations (sys_language_id);
CREATE INDEX sys_translations_org_id ON sys_translations (org_id);

ALTER TABLE sys_access_levels ADD	sys_app_module_id		integer references sys_app_modules;
CREATE INDEX sys_access_levels_sys_app_module_id ON sys_access_levels (sys_app_module_id);

CREATE VIEW vw_sys_app_modules AS
	SELECT sys_apps.sys_app_id, sys_apps.sys_app_name, sys_apps.sys_app_code, sys_apps.sys_app_group,
		sys_app_modules.sys_app_module_id, sys_app_modules.sys_app_module_name, sys_app_modules.price, 
		sys_app_modules.is_default, sys_app_modules.details
	FROM sys_app_modules INNER JOIN sys_apps ON sys_app_modules.sys_app_id = sys_apps.sys_app_id;
	
CREATE VIEW vw_sys_avail_modules AS
	SELECT sys_apps.sys_app_id, sys_apps.sys_app_name, sys_apps.sys_app_code, sys_apps.sys_app_group,
		org_apps.org_app_id, org_apps.org_id,
		sys_app_modules.sys_app_module_id, sys_app_modules.sys_app_module_name, sys_app_modules.price, 
		sys_app_modules.is_default, sys_app_modules.details
	FROM sys_app_modules INNER JOIN sys_apps ON sys_app_modules.sys_app_id = sys_apps.sys_app_id
		INNER JOIN org_apps ON sys_apps.sys_app_id = org_apps.sys_app_id
	WHERE (org_apps.is_active = true);

CREATE VIEW vw_org_apps AS
	SELECT sys_apps.sys_app_id, sys_apps.sys_app_name, sys_apps.sys_app_code, sys_apps.sys_app_group,
		orgs.org_id, orgs.org_name, 
		org_apps.org_app_id, org_apps.price, org_apps.user_accounts, org_apps.created, 
		org_apps.is_montly_bill, org_apps.is_annual_bill, org_apps.details
	FROM org_apps INNER JOIN sys_apps ON org_apps.sys_app_id = sys_apps.sys_app_id
		INNER JOIN orgs ON org_apps.org_id = orgs.org_id;

CREATE VIEW vw_org_app_modules AS
	SELECT vw_sys_app_modules.sys_app_id, vw_sys_app_modules.sys_app_name, vw_sys_app_modules.sys_app_group,
		vw_sys_app_modules.sys_app_module_id, vw_sys_app_modules.sys_app_module_name,
		orgs.org_id, orgs.org_name,  
		org_app_modules.org_app_module_id, org_app_modules.price, org_app_modules.created, 
		org_app_modules.is_active, org_app_modules.user_accounts, org_app_modules.details
	FROM org_app_modules INNER JOIN vw_sys_app_modules ON org_app_modules.sys_app_module_id = vw_sys_app_modules.sys_app_module_id
		INNER JOIN orgs ON org_app_modules.org_id = orgs.org_id;

CREATE VIEW vw_sys_access_levels AS
	SELECT vw_sys_app_modules.sys_app_id, vw_sys_app_modules.sys_app_name,
		vw_sys_app_modules.sys_app_module_id, vw_sys_app_modules.sys_app_module_name,
		use_keys.use_key_id, use_keys.use_key_name,

		sys_access_levels.sys_access_level_id, sys_access_levels.sys_country_id, sys_access_levels.org_id,
		sys_access_levels.sys_access_level_name, sys_access_levels.access_tag, sys_access_levels.acess_details
	FROM sys_access_levels INNER JOIN vw_sys_app_modules ON sys_access_levels.sys_app_module_id = vw_sys_app_modules.sys_app_module_id
		INNER JOIN use_keys ON sys_access_levels.use_key_id = use_keys.use_key_id;

CREATE OR REPLACE FUNCTION add_apps_orgs(varchar(32), varchar(32), varchar(32), varchar(32)) RETURNS varchar(120) AS $$
DECLARE
	v_org_app_id		integer;
	msg					varchar(120);
BEGIN

	IF($3 = '1')THEN
		SELECT org_app_id INTO v_org_app_id
		FROM org_apps
		WHERE (sys_app_id = $1::int) AND (org_id = $4::int);
		
		IF(v_org_app_id is null)THEN
			INSERT INTO org_apps (sys_app_id, org_id)
			VALUES ($1::int, $4::int);
			
			msg := 'App added to organisation';
		ELSE
			msg := 'App already added to organisation';
		END IF;
	ELSIF($3 = '2')THEN
		INSERT INTO org_apps (sys_app_id, org_id)
		SELECT $1::int, orgs.org_id
		FROM orgs LEFT JOIN 
			(SELECT org_id FROM org_apps WHERE sys_app_id = $1::int) as oa
			ON orgs.org_id = oa.org_id
		WHERE (oa.org_id is null);
		msg := 'App added to all organisation';
	END IF;

	RETURN msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION add_modules_orgs(varchar(32), varchar(32), varchar(32), varchar(32)) RETURNS varchar(120) AS $$
DECLARE
	v_sys_app_id		integer;
	v_org_app_id		integer;
	v_org_app_module_id	integer;
	v_price				real;
	msg					varchar(120);
BEGIN

	SELECT sys_app_id, price INTO v_sys_app_id, v_price
	FROM sys_app_modules
	WHERE (sys_app_module_id = $1::int);

	IF($3 = '1')THEN
		SELECT org_app_id INTO v_org_app_id
		FROM org_apps
		WHERE (sys_app_id = v_sys_app_id) AND (org_id = $4::int);

		SELECT org_app_module_id INTO v_org_app_module_id
		FROM org_app_modules
		WHERE (sys_app_module_id = $1::int) AND (org_id = $4::int);

		IF(v_org_app_id is null)THEN
			msg := 'App needs to be added first';
		ELSIF(v_org_app_module_id is not null)THEN
			msg := 'Module already added';
		ELSE
			INSERT INTO org_app_modules (sys_app_module_id, org_id, price, is_active)	
			VALUES ($1::int, $4::int, v_price, true);
			msg := 'Module added';
		END IF;
	ELSIF($3 = '2')THEN
		INSERT INTO org_app_modules (sys_app_module_id, org_id, price, is_active)
		SELECT $1::int, org_apps.org_id, v_price, true
		FROM org_apps LEFT JOIN 
			(SELECT org_id FROM org_app_modules 
				WHERE (org_app_module_id = $1::int)) as oam
			ON org_apps.org_id = oam.org_id
		WHERE (org_apps.sys_app_id = v_sys_app_id)
			AND (oam.org_id is null);
		msg := 'Module added and activated in all organisations';
	ELSIF($3 = '3')THEN
		INSERT INTO org_app_modules (sys_app_module_id, org_id, price, is_active)
		SELECT $1::int, org_apps.org_id, v_price, false
		FROM org_apps LEFT JOIN 
			(SELECT org_id FROM org_app_modules 
				WHERE (org_app_module_id = $1::int)) as oam
			ON org_apps.org_id = oam.org_id
		WHERE (org_apps.sys_app_id = v_sys_app_id)
			AND (oam.org_id is null);
		msg := 'Module added in all organisations';
	END IF;

	RETURN msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION upd_modules_orgs(varchar(32), varchar(32), varchar(32), varchar(32)) RETURNS varchar(120) AS $$
DECLARE
	v_sys_app_id		integer;
	v_org_app_id		integer;
	v_org_app_module_id	integer;
	v_price				real;
	msg					varchar(120);
BEGIN

	IF($3 = '1')THEN
		UPDATE org_app_modules SET is_active = true WHERE (org_app_module_id = $1::int);
		msg := 'Activated module';
	ELSIF($3 = '2')THEN
		UPDATE org_app_modules SET is_active = false WHERE (org_app_module_id = $1::int);
		msg := 'Deactivated module';
	END IF;

	RETURN msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION aft_org_app_modules() RETURNS trigger AS $$
DECLARE
	v_sys_country_id		char(2);
BEGIN

	IF(TG_OP = 'DELETE')THEN
		DELETE FROM sys_access_entitys WHERE sys_access_level_id IN
		(SELECT sys_access_level_id FROM sys_access_levels WHERE (sys_app_module_id = OLD.sys_app_module_id) AND (org_id = OLD.org_id));

		DELETE FROM sys_access_levels WHERE (sys_app_module_id = OLD.sys_app_module_id) AND (org_id = OLD.org_id);
	ELSE
		DELETE FROM sys_access_entitys WHERE sys_access_level_id IN
		(SELECT sys_access_level_id FROM sys_access_levels WHERE (sys_app_module_id = NEW.sys_app_module_id) AND (org_id = NEW.org_id));

		DELETE FROM sys_access_levels WHERE (sys_app_module_id = NEW.sys_app_module_id) AND (org_id = NEW.org_id);
		IF(NEW.is_active = true)THEN
			SELECT default_country_id INTO v_sys_country_id
			FROM orgs WHERE (org_id = NEW.org_id);

			INSERT INTO sys_access_levels (sys_app_module_id, use_key_id, sys_country_id, org_id, sys_access_level_name, access_tag)
			SELECT sys_app_module_id, use_key_id, sys_country_id, NEW.org_id, sys_access_level_name, access_tag
			FROM sys_access_levels
			WHERE (sys_app_module_id = NEW.sys_app_module_id) AND (org_id is null)
				AND (sys_country_id is null);

			IF(v_sys_country_id is not null)THEN
				INSERT INTO sys_access_levels (sys_app_module_id, use_key_id, sys_country_id, org_id, sys_access_level_name, access_tag)
				SELECT sys_app_module_id, use_key_id, sys_country_id, NEW.org_id, sys_access_level_name, access_tag
				FROM sys_access_levels
				WHERE (sys_app_module_id = NEW.sys_app_module_id) AND (org_id is null)
					AND (sys_country_id = v_sys_country_id);
			END IF;
		END IF;
	END IF;

	RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER aft_org_app_modules AFTER INSERT OR UPDATE OR DELETE ON org_app_modules
    FOR EACH ROW EXECUTE PROCEDURE aft_org_app_modules();

INSERT INTO sys_apps (sys_app_id, sys_app_name, sys_app_code, sys_app_group) VALUES
(0, 'Baraza Core', 'baraza', 'baraza'),
(1, 'HR', 'hr', 'business'),
(2, 'Payroll', 'payroll', 'business'),
(3, 'Business', 'business', 'business'),
(4, 'Attendance', 'attendance', 'business'),
(5, 'Projects', 'projects', 'business'),
(6, 'Banking', 'banking', 'finance'),
(7, 'Sacco', 'sacco', 'finance'),
(8, 'Chama', 'chama', 'finance'),
(9, 'Welfare', 'welfare', 'finance'),
(10, 'Property Management', 'property', 'property'),
(11, 'Judiciary', 'judiciary', 'judiciary'),
(15, 'UMIS', 'umis', 'academics'),
(16, 'AIMS', 'aims', 'academics'),
(17, 'School', 'school', 'academics'),
(20, 'Agency', 'agency', 'travel'),
(21, 'TravMIS', 'tmis', 'travel'),
(22, 'Hotel Vouchers', 'voucher', 'travel'),
(23, 'Pick and Drop', 'pnd', 'travel'),
(24, 'Enhanced Client File', 'clientfile', 'travel'),
(25, 'TravDoc', 'travdoc', 'travel'),
(26, 'Corporate SMS', 'sms', 'business'),
(27, 'TravSMS', 'travsms', 'travel'),
(28, 'Vikoba Sacco', 'vikoba', 'finance'),
(29, 'Hotel Reservation', 'hotel', 'travel'),
(30, 'TravMPesa', 'tmpesa', 'travel'),
(31, 'Travel Insurance', 'tinsurance', 'travel');


