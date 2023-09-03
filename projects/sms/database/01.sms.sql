
ALTER TABLE orgs ADD sp_id	varchar(16);
ALTER TABLE orgs ADD service_id	varchar(32);
ALTER TABLE orgs ADD sender_name varchar(16);
ALTER TABLE orgs ADD send_fon varchar(16);
ALTER TABLE orgs ADD sms_rate real default 2 not null;
ALTER TABLE orgs ADD show_fare boolean default false;
ALTER TABLE orgs ADD gds_free_field integer default 96;
ALTER TABLE orgs ADD sc_register char(1) default 'f' not null;
ALTER TABLE orgs ADD ait_username varchar(64);
ALTER TABLE orgs ADD ait_app_key varchar(64);
ALTER TABLE orgs ADD alphanumeric varchar(64);
ALTER TABLE orgs ADD air_touch_key varchar(64);
ALTER TABLE orgs ADD provider_id integer default 0 not null;

ALTER TABLE address ADD CONSTRAINT address_org_id_mobile_key UNIQUE (org_id, mobile);

ALTER TABLE entitys ADD son varchar(6);
ALTER TABLE entitys ADD phone_ph boolean default true;
ALTER TABLE entitys ADD phone_pa boolean default false;
ALTER TABLE entitys ADD phone_pb boolean default false;
ALTER TABLE entitys ADD phone_pt boolean default false;

CREATE TABLE firms (
	firm_id					serial primary key,
	org_id					integer references orgs,
	firm_name				varchar(120),
	details					text
);
CREATE INDEX firms_org_id ON firms (org_id);

ALTER TABLE address ADD firm_id	integer references firms;
CREATE INDEX address_firm_id ON address (firm_id);

CREATE TABLE sms_trans (
	sms_trans_id			serial primary key,
	org_id					integer references orgs,
	message					varchar(2400),
	origin					varchar(50),
	sms_time				timestamp,
	client_id				varchar(50),
	msg_number				varchar(50),
	code					varchar(25),
	amount					real,
	in_words				varchar(240),
	narrative				varchar(240),
	sms_id					integer,
	sms_deleted				boolean default false not null,
	sms_picked				boolean default false not null,
	part_id					integer,
	part_message			varchar(240),
	part_no					integer,
	part_count				integer,
	complete				boolean default false,
	UNIQUE(origin, sms_time)
);
CREATE INDEX sms_trans_org_id ON sms_trans (org_id);

CREATE TABLE address_groups (
	address_group_id		serial primary key,
	org_id					integer references orgs,
	address_group_name		varchar(50),
	details					text
);
CREATE INDEX address_groups_org_id ON address_groups (org_id);

CREATE TABLE address_members (
	address_member_id		serial primary key,
	address_group_id		integer references address_groups,
	address_id				integer references address,
	org_id					integer references orgs,
	is_active				boolean default true,
	narrative				varchar(240),
	UNIQUE(address_group_id, address_id)
);
CREATE INDEX address_members_address_group_id ON address_members (address_group_id);
CREATE INDEX address_members_address_id ON address_members (address_id);
CREATE INDEX address_members_org_id ON address_members (org_id);

CREATE TABLE folders (
	folder_id				serial primary key,
	org_id					integer references orgs,
	folder_name				varchar(25) unique,
	details					text
);
CREATE INDEX folders_org_id ON folders (org_id);
INSERT INTO folders (folder_id, folder_name) VALUES (0, 'Outbox');
INSERT INTO folders (folder_id, folder_name) VALUES (1, 'Draft');
INSERT INTO folders (folder_id, folder_name) VALUES (2, 'Sent');
INSERT INTO folders (folder_id, folder_name) VALUES (3, 'Inbox');
INSERT INTO folders (folder_id, folder_name) VALUES (4, 'Action');

CREATE TABLE sms (
	sms_id					serial primary key,
	folder_id				integer references folders,
	address_group_id		integer references address_groups,
	entity_id				integer references entitys,
	org_id					integer references orgs,
	sms_origin				varchar(50),
	sms_number				varchar(25),
	sms_numbers				text,
	sms_time				timestamp default now(),
	sms_count				integer not null default 0,
	number_error			boolean default false,
	message_ready			boolean default false,
	sent					boolean default false,
	retries					integer default 0 not null,
	last_retry				timestamp default now(),
	
	addresses				text,
	senderAddress			varchar(64),
	serviceId				varchar(64), 
	spRevpassword			varchar(64), 
	dateTime				timestamp, 
	correlator				varchar(64), 
	traceUniqueID			varchar(64), 
	linkid					varchar(64), 
	spRevId					varchar(64), 
	spId					varchar(64), 
	smsServiceActivationNumber	varchar(64),

	link_id					integer,

	message					text,
	details					text
);
CREATE INDEX sms_folder_id ON sms (folder_id);
CREATE INDEX sms_address_group_id ON sms (address_group_id);
CREATE INDEX sms_entity_id ON sms (entity_id);
CREATE INDEX sms_org_id ON sms (org_id);

CREATE TABLE sms_address (
	sms_address_id			serial primary key,
	sms_id					integer references sms,
	address_id				integer references address,
	org_id					integer references orgs,
	narrative				varchar(50),
	UNIQUE(sms_id, address_id)
);
CREATE INDEX sms_address_sms_id ON sms_address (sms_id);
CREATE INDEX sms_address_address_id ON sms_address (address_id);
CREATE INDEX sms_address_org_id ON sms_address (org_id);

CREATE TABLE sms_queue (
	sms_queue_id			serial primary key,
	sms_id					integer references sms,
	org_id					integer references orgs,
	send_results			varchar(64),
	message_sent			boolean default false not null,
	status_code				varchar(4),
	delivery_status			varchar(64),
	trace_id				varchar(32),
	message_parts			integer default 1 not null,
	sms_number				varchar(25),
	sms_price				real default 2 not null,
	retries					integer default 0 not null,
	last_retry				timestamp default now(),
	delivery_time			timestamp
);
CREATE INDEX sms_queue_sms_id ON sms_queue (sms_id);
CREATE INDEX sms_queue_org_id ON sms_queue (org_id);

CREATE TABLE numbers_imports (
	numbers_import_id		serial primary key,
	address_group_id		integer references address_groups,
	entity_id				integer references entitys,
	org_id					integer references orgs,
	number_name				varchar(120),
	mobile_number			varchar(50)
);
CREATE INDEX numbers_imports_address_group_id ON numbers_imports (address_group_id);
CREATE INDEX numbers_imports_entity_id ON numbers_imports (entity_id);
CREATE INDEX numbers_imports_org_id ON numbers_imports (org_id);

CREATE TABLE sms_issues (
	sms_issue_id			serial primary key,
	org_id					integer references orgs,
	resolved				boolean default false not null,
	created					timestamp default now() not null,
	narrative				text
);
CREATE INDEX sms_issues_org_id ON sms_issues (org_id);

CREATE TABLE sms_configs (
	sms_config_id			serial primary key,
	last_sent				timestamp,
	send_error				boolean,
	error_email				boolean,
	email_time				timestamp,
	send_code				varchar(64),
	narrative				varchar(200)
);
INSERT INTO sms_configs (sms_config_id, last_sent, send_error, error_email, email_time, narrative)
VALUES (0, current_timestamp, false, false, current_timestamp, null);

CREATE TABLE load_units (
	load_unit_id			serial primary key,
	org_id					integer references orgs,
	load_date				date not null,
	expiry_date				date not null,
	load_amount				real not null,
	units					integer default 0  not null,
	sold_units				integer default 0  not null,
	details					text
);
CREATE INDEX load_units_org_id ON load_units (org_id);

	
DROP VIEW vw_entitys;
DROP VIEW vw_orgs;
DROP VIEW vw_entity_address;
DROP VIEW vw_org_address;
DROP VIEW vw_address;

CREATE VIEW vw_address AS
	SELECT sys_countrys.sys_country_id, sys_countrys.sys_country_name, address.address_id, address.org_id, address.address_name,
		address.table_name, address.table_id, address.post_office_box, address.postal_code, address.premises, address.street, address.town,
		address.phone_number, address.extension, address.mobile, address.fax, address.email, address.is_default, address.website, address.details,
		address_types.address_type_id, address_types.address_type_name,
		firms.firm_id, firms.firm_name,
		(COALESCE(firms.firm_name || ', ', '') ||  address.address_name) as disp_name

	FROM address INNER JOIN sys_countrys ON address.sys_country_id = sys_countrys.sys_country_id
		LEFT JOIN address_types ON address.address_type_id = address_types.address_type_id
		LEFT JOIN firms ON address.firm_id = firms.firm_id;

CREATE VIEW vw_org_address AS
	SELECT vw_address.sys_country_id as org_sys_country_id, vw_address.sys_country_name as org_sys_country_name,
		vw_address.address_id as org_address_id, vw_address.table_id as org_table_id, vw_address.table_name as org_table_name,
		vw_address.post_office_box as org_post_office_box, vw_address.postal_code as org_postal_code,
		vw_address.premises as org_premises, vw_address.street as org_street, vw_address.town as org_town,
		vw_address.phone_number as org_phone_number, vw_address.extension as org_extension,
		vw_address.mobile as org_mobile, vw_address.fax as org_fax, vw_address.email as org_email,
		vw_address.website as org_website
	FROM vw_address
	WHERE (vw_address.table_name = 'orgs') AND (vw_address.is_default = true);
	
CREATE VIEW vw_address_entitys AS
	SELECT vw_address.address_id, vw_address.address_name, vw_address.table_id, vw_address.table_name,
		vw_address.sys_country_id, vw_address.sys_country_name, vw_address.is_default,
		vw_address.post_office_box, vw_address.postal_code, vw_address.premises, vw_address.street, vw_address.town, 
		vw_address.phone_number, vw_address.extension, vw_address.mobile, vw_address.fax, vw_address.email, vw_address.website
	FROM vw_address
	WHERE (vw_address.table_name = 'entitys') AND (vw_address.is_default = true);

CREATE VIEW vw_entity_address AS
	SELECT vw_address.address_id, vw_address.address_name,
		vw_address.sys_country_id, vw_address.sys_country_name, vw_address.table_id, vw_address.table_name,
		vw_address.is_default, vw_address.post_office_box, vw_address.postal_code, vw_address.premises,
		vw_address.street, vw_address.town, vw_address.phone_number, vw_address.extension, vw_address.mobile,
		vw_address.fax, vw_address.email, vw_address.website
	FROM vw_address
	WHERE (vw_address.table_name = 'entitys') AND (vw_address.is_default = true);

CREATE VIEW vw_orgs AS
	SELECT orgs.org_id, orgs.org_name, orgs.is_default, orgs.is_active, orgs.logo, 
		orgs.pin, orgs.pcc, orgs.sp_id, orgs.service_id, orgs.sender_name, 
		orgs.sms_rate, orgs.show_fare, orgs.gds_free_field, orgs.details,

		vw_org_address.org_sys_country_id, vw_org_address.org_sys_country_name,
		vw_org_address.org_address_id, vw_org_address.org_table_name,
		vw_org_address.org_post_office_box, vw_org_address.org_postal_code,
		vw_org_address.org_premises, vw_org_address.org_street, vw_org_address.org_town,
		vw_org_address.org_phone_number, vw_org_address.org_extension,
		vw_org_address.org_mobile, vw_org_address.org_fax, vw_org_address.org_email, vw_org_address.org_website
	FROM orgs LEFT JOIN vw_org_address ON orgs.org_id = vw_org_address.org_table_id;

CREATE VIEW vw_travdoc_user AS 
	SELECT vw_orgs.org_id,vw_orgs.org_name,vw_orgs.pcc,vw_orgs.gds_free_field,vw_orgs.show_fare, vw_orgs.logo,vw_entity_address.table_id,
    	vw_entity_address.table_name, vw_entity_address.post_office_box, vw_entity_address.postal_code,vw_entity_address.premises,
	    vw_entity_address.street, vw_entity_address.town, vw_entity_address.phone_number, vw_entity_address.email,vw_entity_address.sys_country_name,
    	entitys.entity_id, entitys.entity_name, entitys.son, entitys.phone_ph, entitys.phone_pa, entitys.phone_pb,entitys.phone_pt
	FROM entitys
		LEFT JOIN vw_entity_address ON entitys.entity_id = vw_entity_address.table_id
		JOIN vw_orgs ON entitys.org_id = vw_orgs.org_id
		JOIN entity_types ON entitys.entity_type_id = entity_types.entity_type_id;

CREATE VIEW vw_sms_entitys AS
	SELECT orgs.org_id, orgs.org_name, orgs.is_default as org_is_default, 
		orgs.is_active as org_is_active, orgs.logo as org_logo, 
		orgs.pcc, orgs.sp_id, orgs.service_id, orgs.sender_name,

		vw_entity_address.address_id, vw_entity_address.address_name,
		vw_entity_address.sys_country_id, vw_entity_address.sys_country_name, vw_entity_address.table_name, 
		vw_entity_address.is_default, vw_entity_address.post_office_box, vw_entity_address.postal_code, 
		vw_entity_address.premises, vw_entity_address.street, vw_entity_address.town, 
		vw_entity_address.phone_number, vw_entity_address.extension, vw_entity_address.mobile, 
		vw_entity_address.fax, vw_entity_address.email, vw_entity_address.website,

		entitys.entity_id, entitys.entity_name, entitys.user_name, entitys.super_user, entitys.entity_leader, 
		entitys.date_enroled, entitys.is_active, entitys.entity_password, entitys.first_password, 
		entitys.function_role, entitys.primary_email, entitys.primary_telephone,
		entity_types.entity_type_id, entity_types.entity_type_name, 
		entity_types.entity_role, entity_types.use_key_id, entitys.son
	FROM (entitys LEFT JOIN vw_entity_address ON entitys.entity_id = vw_entity_address.table_id)
		INNER JOIN orgs ON entitys.org_id = orgs.org_id
		INNER JOIN entity_types ON entitys.entity_type_id = entity_types.entity_type_id;

CREATE VIEW vw_address_members AS
	SELECT address_types.address_type_id, address_types.address_type_name,
		firms.firm_id, firms.firm_name,
		sys_countrys.sys_country_id, sys_countrys.sys_country_name,
		(COALESCE(firms.firm_name || ', ', '') ||  address.address_name) as disp_name,
		address.address_id, address.address_name, address.mobile, address.email,
		address_groups.address_group_id, address_groups.address_group_name, 
		address_members.org_id, address_members.address_member_id, address_members.is_active, 
		address_members.narrative
	FROM address_members INNER JOIN address ON address_members.address_id = address.address_id
		INNER JOIN address_groups ON address_members.address_group_id = address_groups.address_group_id
		INNER JOIN sys_countrys ON address.sys_country_id = sys_countrys.sys_country_id
		LEFT JOIN address_types ON address.address_type_id = address_types.address_type_id
		LEFT JOIN firms ON address.firm_id = firms.firm_id;

CREATE VIEW vw_sms AS
	SELECT folders.folder_id, folders.folder_name, sms.sms_id,
		sms.sms_number, sms.message_ready, sms.sent, sms.message, sms.details,
		sms.org_id, vw_address.address_name,
		to_char(sms.sms_time, 'yyyy-mm-dd'::text) AS date, sms.sms_count,
		address_groups.address_group_id, address_groups.address_group_name
	FROM sms INNER JOIN folders ON sms.folder_id = folders.folder_id
		LEFT JOIN vw_address ON (sms.sms_number = vw_address.mobile) AND (sms.org_id = vw_address.org_id)
		LEFT JOIN address_groups ON sms.address_group_id = address_groups.address_group_id;

CREATE VIEW vw_usage AS
	SELECT sms.sms_id, to_char(sms.sms_time, 'yyyy-mm-dd'::text) AS date,
		sms.sms_count, sms.entity_id, sms.org_id, sms.sent, entitys.son, orgs.pcc
	FROM sms JOIN entitys ON entitys.entity_id = sms.entity_id
		JOIN orgs ON orgs.org_id = sms.org_id;

CREATE VIEW vw_sms_address AS
	SELECT folders.folder_id, folders.folder_name, sms.sms_id, sms.sms_number, 
		sms.message_ready, sms.sent, sms.message,
		address.address_id, address.address_name, address.mobile,
		sms_address.sms_address_id, sms_address.org_id, sms_address.narrative
	FROM sms INNER JOIN folders ON sms.folder_id = folders.folder_id
		INNER JOIN sms_address ON sms.sms_id = sms_address.sms_id
		INNER JOIN address ON sms_address.address_id = address.address_id;
	
CREATE VIEW vw_sms_units AS
	SELECT orgs.org_id, orgs.org_name, 
		sms_queue.last_retry::date as sms_day, sum(sms_queue.message_parts) as sms_count
	FROM orgs INNER JOIN sms_queue ON orgs.org_id = sms_queue.org_id
	GROUP BY orgs.org_id, orgs.org_name, sms_queue.last_retry::date;
	
CREATE VIEW vw_load_units AS
	SELECT orgs.org_id, orgs.org_name,
		load_units.load_unit_id, load_units.load_date, load_units.expiry_date, load_units.load_amount,
		load_units.units, load_units.sold_units, load_units.details
	FROM orgs INNER JOIN load_units ON orgs.org_id = load_units.org_id;

CREATE VIEW vw_load_usage AS
	(SELECT orgs.org_id, orgs.org_name, 
		sms_queue.last_retry::date as sms_day, sum(sms_queue.message_parts) as sms_count,
		'0'::integer as sms_units, '0'::integer as sms_sold
	FROM orgs INNER JOIN sms_queue ON orgs.org_id = sms_queue.org_id
	GROUP BY orgs.org_id, orgs.org_name, sms_queue.last_retry::date)
	UNION
	(SELECT orgs.org_id, orgs.org_name, 
		load_units.load_date, '0'::integer, sum(load_units.units), sum(sold_units)
	FROM orgs INNER JOIN load_units ON orgs.org_id = load_units.org_id
	GROUP BY orgs.org_id, orgs.org_name, load_units.load_date);

CREATE VIEW vw_unit_year AS
	SELECT to_char(sms_day, 'YYYY') as unit_year
	FROM vw_load_usage
	GROUP BY to_char(sms_day, 'YYYY')
	ORDER BY to_char(sms_day, 'YYYY');
	
CREATE VIEW vw_unit_months AS
	SELECT to_char(sms_day, 'YYYY') as unit_year, to_char(sms_day, 'YYYYMM') as unit_month,
		to_char(sms_day, 'Month') as unit_month_name
	FROM vw_load_usage
	GROUP BY to_char(sms_day, 'YYYY'), to_char(sms_day, 'YYYYMM'), to_char(sms_day, 'Month')
	ORDER BY to_char(sms_day, 'YYYY'), to_char(sms_day, 'YYYYMM'), to_char(sms_day, 'Month');
	
	
CREATE OR REPLACE FUNCTION ins_sms_trans() RETURNS trigger AS $$
DECLARE
	rec RECORD;
	msg varchar(2400);
BEGIN
	IF(NEW.part_no = NEW.part_count) THEN
		IF(NEW.part_no = 1) THEN
			INSERT INTO sms (folder_id, sms_number, message)
			VALUES(3, NEW.origin, NEW.message);

			NEW.sms_picked = true;
		ELSE
			msg := '';
			FOR rec IN SELECT part_no, message FROM sms_trans WHERE (part_id = NEW.part_id) AND (origin = NEW.origin) AND (sms_picked = false)
			ORDER BY part_no LOOP
				msg := msg || rec.message;
			END LOOP;
			msg := msg || NEW.message;

			INSERT INTO sms (folder_id, sms_number, message)
			VALUES(3, NEW.origin, msg);

			UPDATE sms_trans SET sms_picked = true WHERE (part_id = NEW.part_id) AND (origin = NEW.origin) AND (sms_picked = false);
			NEW.sms_picked = true;
		END IF;
	END IF;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ins_sms_trans BEFORE INSERT ON sms_trans
    FOR EACH ROW EXECUTE PROCEDURE ins_sms_trans();

CREATE OR REPLACE FUNCTION ins_sms() RETURNS trigger AS $$
BEGIN
	
	IF(NEW.addresses is not null)THEN
		IF(NEW.sms_numbers is null) THEN
			NEW.sms_numbers := NEW.addresses;
		ELSE
			NEW.sms_numbers := NEW.sms_numbers || ',' || NEW.addresses;
		END IF;
	END IF;
	
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ins_sms BEFORE INSERT OR UPDATE ON sms
    FOR EACH ROW EXECUTE PROCEDURE ins_sms();

CREATE OR REPLACE FUNCTION aft_sms() RETURNS trigger AS $$
BEGIN
	IF (NEW.smsServiceActivationNumber = 'tel:20583') THEN
		INSERT INTO sms (org_id, folder_id, sms_origin, sms_number, linkid, message_ready, message)
		VALUES (0, 0, '20583', '254' || replace(NEW.senderAddress, 'tel:', ''), NEW.linkid, true, 'Thank you for contacting the Judiciary Service Desk. Your submission is being attended to. For further assistance call 020 2221221.');

		INSERT INTO sys_emailed (org_id, sys_email_id, table_name, table_id)
		VALUES (0, 1, 'sms', NEW.sms_id);
	END IF;

	RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER aft_sms AFTER INSERT ON sms
    FOR EACH ROW EXECUTE PROCEDURE aft_sms();

CREATE OR REPLACE FUNCTION ins_member_address(varchar(12), varchar(32), varchar(32)) RETURNS varchar(120) AS $$
DECLARE
	v_address_member_id		integer;
	v_org_id				integer;
	msg 					varchar(120);
BEGIN

	SELECT org_id INTO v_org_id
	FROM address_groups WHERE address_group_id = CAST($3 as int);

	SELECT address_member_id INTO v_address_member_id
	FROM address_members
	WHERE (address_id = CAST($1 as int)) AND (address_group_id = CAST($3 as int));

	IF(v_address_member_id is null)THEN
		INSERT INTO address_members (address_group_id, address_id, org_id, is_active)
		VALUES(CAST($3 as int), CAST($1 as int), v_org_id, true);
		msg := 'Address added';
	ELSE
		msg := 'No duplicates address allowed';
		RAISE EXCEPTION 'No duplicates address allowed';
	END IF;

	return msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ins_sms_address(varchar(12), varchar(32), varchar(32)) RETURNS varchar(120) AS $$
DECLARE
	v_sms_address_id		integer;
	v_org_id				integer;
	msg 					varchar(120);
BEGIN
	SELECT org_id INTO v_org_id
	FROM sms WHERE sms_id = CAST($3 as int);

	SELECT sms_address_id INTO v_sms_address_id
	FROM sms_address
	WHERE (address_id = CAST($1 as int)) AND (sms_id = CAST($3 as int));

	IF(v_sms_address_id is null)THEN
		INSERT INTO sms_address (sms_id, address_id, org_id)
		VALUES(CAST($3 as int), CAST($1 as int), v_org_id);
		msg := 'Address Added';
	ELSE
		msg := 'No duplicates address allowed';
		RAISE EXCEPTION 'No duplicates address allowed';
	END IF;

	return msg;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION aft_sms_queue() RETURNS trigger AS $$
BEGIN
	
	IF(NEW.send_results = 'INSUF100')THEN
		INSERT INTO sms_issues (org_id, narrative)
		VALUES (NEW.org_id, 'Load airtel credit');
	END IF;
	
	RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER aft_sms_queue AFTER INSERT OR UPDATE ON sms_queue
    FOR EACH ROW EXECUTE PROCEDURE aft_sms_queue();
    
    
