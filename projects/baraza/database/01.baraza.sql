CREATE TABLE sys_menu_msg (
	sys_menu_msg_id			serial primary key,
	menu_id					varchar(16) not null,
	menu_name				varchar(50) not null,
	xml_file				varchar(50) not null,
	msg						text
);

CREATE TABLE sys_audit_trail (
	sys_audit_trail_id		serial primary key,
	user_id					varchar(50) not null,
	user_ip					varchar(50),
	change_date				timestamp default now() not null,
	table_name				varchar(50) not null,
	record_id				varchar(50) not null,
	change_type				varchar(50) not null,
	narrative				varchar(240)
);

CREATE TABLE sys_audit_details (
	sys_audit_trail_id		integer references sys_audit_trail primary key,
	old_value				text
);

CREATE TABLE sys_errors (
	sys_error_id			serial primary key,
	sys_error				varchar(240) not null,
	error_message			text not null
);

CREATE TABLE sys_continents (
	sys_continent_id		char(2) primary key,
	sys_continent_name		varchar(120) unique
);

CREATE TABLE sys_countrys (
	sys_country_id			char(2) primary key,
	sys_continent_id		char(2) references sys_continents,
	sys_country_code		varchar(3),
	sys_country_name		varchar(120) not null unique,
	sys_country_number		varchar(3),
	sys_country_capital		varchar(64),
	sys_phone_code			varchar(7),
	sys_currency_name		varchar(50),
	sys_currency_code		varchar(3),
	sys_currency_cents		varchar(50),
	sys_currency_exchange	real
);
CREATE INDEX sys_countrys_sys_continent_id ON sys_countrys (sys_continent_id);

CREATE TABLE sys_nationalitys (
	sys_nationality_id		varchar(3),
	sys_nationality_name	varchar(100)
);

CREATE TABLE currency (
	currency_id				serial primary key,
	currency_name			varchar(50) not null,
	currency_symbol			varchar(3) not null
);

CREATE TABLE use_keys (
	use_key_id				integer primary key,
	use_key_name			varchar(32) not null,
	use_function			integer
);

CREATE TABLE sys_languages (
	sys_language_id			serial primary key,
	sys_language_name		varchar(50) not null unique
);

CREATE TABLE orgs (
	org_id					serial primary key,
	currency_id				integer references currency,
	default_country_id		char(2) references sys_countrys,
	parent_org_id			integer references orgs,
	org_name				varchar(50) not null unique,
	org_full_name			varchar(120),
	org_sufix				varchar(32) not null unique,
	is_default				boolean default true not null,
	is_active				boolean default true not null,
	department_filter		boolean default false not null,
	deployment_filter		boolean default false not null,
	pin 					varchar(50),
	pcc						varchar(12),

	logo					varchar(50),
	letter_head				varchar(50),
	email_from				varchar(120),
	web_logos				boolean default false not null,
	logo_path				varchar(120),

	created					timestamp default current_timestamp not null,
	password_scheme			integer default 1 not null,
	no_of_users				integer default 1 not null,
	system_key				varchar(64),
	system_identifier		varchar(64),
	MAC_address				varchar(64),
	public_key				bytea,
	license					bytea,

	details					text
);
CREATE INDEX orgs_currency_id ON orgs (currency_id);
CREATE INDEX orgs_parent_org_id ON orgs (parent_org_id);
CREATE INDEX orgs_default_country_id ON orgs(default_country_id);

ALTER TABLE currency ADD org_id			integer references orgs;
CREATE INDEX currency_org_id ON currency (org_id);

CREATE TABLE currency_rates (
	currency_rate_id		serial primary key,
	currency_id				integer references currency,
	org_id					integer references orgs,
	exchange_date			date default current_date not null,
	exchange_rate			real default 1 not null
);
CREATE INDEX currency_rates_org_id ON currency_rates (org_id);
CREATE INDEX currency_rates_currency_id ON currency_rates (currency_id);

CREATE TABLE sys_configs (
	sys_config_id			serial primary key,
	org_id					integer references orgs,
	config_type_id			integer not null,
	config_name				varchar(254) not null unique,
	config_value			text not null,
	config_amount			real
);
CREATE INDEX sys_configs_org_id ON sys_configs (org_id);

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

CREATE TABLE sys_queries (
	sys_queries_id			serial primary key,
	org_id					integer references orgs,
	sys_query_Name			varchar(50),
	query_date				timestamp not null default now(),
	query_text				text,
	query_params			text,
	UNIQUE(org_id, sys_query_Name)
);
CREATE INDEX sys_queries_org_id ON sys_queries (org_id);

CREATE TABLE sys_news (
	sys_news_id				serial primary key,
	org_id					integer references orgs,
	sys_news_group			integer,
	sys_news_title			varchar(240) not null,
	publish					boolean default false not null,
	details					text
);
CREATE INDEX sys_news_org_id ON sys_news (org_id);

CREATE TABLE sys_files (
	sys_file_id				serial primary key,
	org_id					integer references orgs,
	table_id				integer,
	table_name				varchar(50),
	file_name				varchar(320),
	file_type				varchar(320),
	file_size				integer,
	narrative				varchar(320),
	details					text,
	created					timestamp default current_timestamp not null
);
CREATE INDEX sys_files_org_id ON sys_files (org_id);
CREATE INDEX sys_files_table_id ON sys_files (table_id);

CREATE TABLE address_types (
	address_type_id			serial primary key,
	org_id					integer references orgs,
	address_type_name		varchar(50)
);
CREATE INDEX address_types_org_id ON address_types (org_id);

CREATE TABLE address (
	address_id				serial primary key,
	address_type_id			integer references address_types,
	sys_country_id			char(2) references sys_countrys,
	org_id					integer references orgs,
	address_name			varchar(120),
	table_name				varchar(32),
	table_id				integer,
	post_office_box			varchar(50),
	postal_code				varchar(12),
	premises				varchar(120),
	street					varchar(120),
	town					varchar(50),
	phone_number			varchar(150),
	extension				varchar(15),
	mobile					varchar(150),
	fax						varchar(150),
	email					varchar(120),
	website					varchar(120),
	is_default				boolean default false not null,
	first_password			varchar(32),
	details					text
);
CREATE INDEX address_address_type_id ON address (address_type_id);
CREATE INDEX address_sys_country_id ON address (sys_country_id);
CREATE INDEX address_org_id ON address (org_id);
CREATE INDEX address_table_name ON address (table_name);
CREATE INDEX address_table_id ON address (table_id);

CREATE TABLE entity_types (
	entity_type_id			serial primary key,
	use_key_id				integer not null references use_keys,
	org_id					integer references orgs,
	entity_type_name		varchar(50) not null,
	entity_role				varchar(240),
	start_view				varchar(120),
	group_email				varchar(120),
	description				text,
	details					text,
	UNIQUE(org_id, entity_type_name)
);
CREATE INDEX entity_types_use_key_id ON entity_types (use_key_id);
CREATE INDEX entity_types_org_id ON entity_types (org_id);

CREATE TABLE entitys (
	entity_id				serial primary key,
	entity_type_id			integer not null references entity_types,
	use_key_id				integer not null references use_keys,
	sys_language_id			integer references sys_languages,
	org_id					integer not null references orgs,
	entity_name				varchar(120) not null,
	user_name				varchar(120) not null unique,
	primary_email			varchar(120),
	primary_telephone		varchar(50),
	entity_tag				varchar(32),
	super_user				boolean default false not null,
	entity_leader			boolean default false not null,
	no_org					boolean default false not null,
	function_role			varchar(240),
	date_enroled			timestamp default now() not null,
	is_active				boolean default true,
	last_login				timestamp,
	entity_password			varchar(64) not null,
	first_password			varchar(64) not null,
	new_password			varchar(64),
	password_code			varchar(32),
	start_url				varchar(64),
	is_picked				boolean default false not null,
	locked_until			timestamp,
	details					text
);
CREATE INDEX entitys_entity_type_id ON entitys (entity_type_id);
CREATE INDEX entitys_use_key_id ON entitys (use_key_id);
CREATE INDEX entitys_user_name ON entitys (user_name);
CREATE INDEX entitys_sys_language_id ON entitys (sys_language_id);
CREATE INDEX entitys_org_id ON entitys (org_id);

CREATE TABLE entity_fields (
	entity_field_id			serial primary key,
	org_id					integer not null references orgs,
	use_type				integer default 1 not null,
	is_active				boolean default true,
	entity_field_name		varchar(240),
	entity_field_source		varchar(320)
);
CREATE INDEX entity_fields_org_id ON entity_fields (org_id);

CREATE TABLE entity_values (
	entity_value_id			serial primary key,
	entity_id				integer references entitys,
	entity_field_id			integer references entity_fields,
	org_id					integer references orgs,
	entity_value			varchar(240),
	UNIQUE(entity_id, entity_field_id)
);
CREATE INDEX entity_values_entity_id ON entity_values (entity_id);
CREATE INDEX entity_values_entity_field_id ON entity_values (entity_field_id);
CREATE INDEX entity_values_org_id ON entity_values (org_id);

CREATE TABLE entity_subscriptions (
	entity_subscription_id	serial primary key,
	entity_type_id			integer not null references entity_types,
	entity_id				integer not null references entitys,
	org_id					integer references orgs,
	details					text,
	UNIQUE(entity_id, entity_type_id)
);
CREATE INDEX entity_subscriptions_entity_type_id ON entity_subscriptions (entity_type_id);
CREATE INDEX entity_subscriptions_entity_id ON entity_subscriptions (entity_id);
CREATE INDEX entity_subscriptions_org_id ON entity_subscriptions (org_id);

CREATE TABLE entity_orgs (
	entity_org_id			serial primary key,
	entity_id				integer not null references entitys,
	org_id					integer references orgs,
	details					text,
	UNIQUE(entity_id, org_id)
);
CREATE INDEX entity_orgs_entity_id ON entity_orgs (entity_id);
CREATE INDEX entity_orgs_org_id ON entity_orgs (org_id);

CREATE TABLE entity_reset (
	entity_reset_id			serial primary key,
	entity_id				integer references entitys,
	org_id					integer references orgs,
	request_email			varchar(320),
	request_time			timestamp default now(),
	login_ip				varchar(64),
	phone_serial_number		varchar(50),
	narrative				varchar(240)
);
CREATE INDEX entity_reset_entity_id ON entity_reset (entity_id);
CREATE INDEX entity_reset_org_id ON entity_reset (org_id);

CREATE TABLE sys_access_levels (
	sys_access_level_id		serial primary key,
	sys_app_module_id		integer references sys_app_modules,
	use_key_id				integer references use_keys,
	sys_country_id			char(2) references sys_countrys,
	org_id					integer references orgs,
	sys_access_level_name	varchar(64) not null,
	access_tag				varchar(32) not null,
	acess_details			text,
	UNIQUE(org_id, sys_access_level_name)
);
CREATE INDEX sys_access_levels_use_key_id ON sys_access_levels (use_key_id);
CREATE INDEX sys_access_levels_sys_app_module_id ON sys_access_levels (sys_app_module_id);
CREATE INDEX sys_access_levels_sys_country_id ON sys_access_levels (sys_country_id);
CREATE INDEX sys_access_levels_org_id ON sys_access_levels (org_id);

CREATE TABLE sys_access_entitys (
	sys_access_entity_id	serial primary key,
	sys_access_level_id		integer not null references sys_access_levels,
	entity_id				integer not null references entitys,
	org_id					integer references orgs,
	narrative				varchar(320),
	granted_on				timestamp default current_timestamp not null,
	UNIQUE(sys_access_level_id, entity_id)
);
CREATE INDEX sys_access_entitys_sys_access_level_id ON sys_access_entitys (sys_access_level_id);
CREATE INDEX sys_access_entitys_entity_id ON sys_access_entitys (entity_id);
CREATE INDEX sys_access_entitys_org_id ON sys_access_entitys (org_id);

CREATE TABLE reporting (
	reporting_id			serial primary key,
	entity_id				integer references entitys,
	report_to_id			integer references entitys,
	org_id					integer references orgs,
	date_from				date,
	date_to					date,
	reporting_level			integer default 1 not null,
	primary_report			boolean default true not null,
	is_active				boolean default true not null,
	ps_reporting			real,
	details					text,

	UNIQUE(entity_id, report_to_id, reporting_level)
);
CREATE INDEX reporting_entity_id ON reporting(entity_id);
CREATE INDEX reporting_report_to_id ON reporting(report_to_id);
CREATE INDEX reporting_org_id ON reporting(org_id);

CREATE TABLE sys_logins (
	sys_login_id			serial primary key,
	entity_id				integer references entitys,
	sys_app_id				integer references sys_apps,
	login_time				timestamp default now(),
	login_ip				varchar(64),
	phone_serial_number		varchar(50),
	correct_login			boolean default true not null,
	narrative				varchar(240)
);
CREATE INDEX sys_logins_entity_id ON sys_logins (entity_id);
CREATE INDEX sys_logins_sys_app_id ON sys_logins (sys_app_id);

CREATE TABLE sys_err_logins (
	sys_err_login_id		serial primary key,
	user_name				varchar(50),
	login_time				timestamp default now(),
	login_ip				varchar(64),
	phone_serial_number		varchar(50)
);

CREATE TABLE sys_reset (
	sys_reset_id			serial primary key,
	entity_id				integer references entitys,
	org_id					integer references orgs,
	request_email			varchar(320),
	request_phone			varchar(120),
	password_code			varchar(32),
	request_time			timestamp default now(),
	login_ip				varchar(64),
	narrative				varchar(240)
);
CREATE INDEX sys_reset_entity_id ON sys_reset (entity_id);
CREATE INDEX sys_reset_org_id ON sys_reset (org_id);

CREATE TABLE sys_dashboard (
	sys_dashboard_id		serial primary key,
	entity_id				integer references entitys,
	org_id					integer references orgs,
	narrative				varchar(240),
	details					text
);
CREATE INDEX sys_dashboard_entity_id ON sys_dashboard (entity_id);
CREATE INDEX sys_dashboard_org_id ON sys_dashboard (org_id);

CREATE TABLE sys_emails (
	sys_email_id			serial primary key,
	org_id					integer references orgs,
	use_type				integer default 1 not null,
	sys_email_name			varchar(50),
	default_email			varchar(320),
	title					varchar(240) not null,
	details					text,
	UNIQUE(org_id, use_type)
);
CREATE INDEX sys_emails_org_id ON sys_emails (org_id);

CREATE TABLE sys_emailed (
	sys_emailed_id			serial primary key,
	sys_email_id			integer references sys_emails,
	org_id					integer references orgs,
	table_id				integer,
	table_name				varchar(50),
	email_type				integer default 1 not null,
	emailed					boolean default false not null,
	send_status				integer default 0 not null,
	created					timestamp default current_timestamp not null,
	narrative				varchar(240),
	mail_body				text
);
CREATE INDEX sys_emailed_sys_email_id ON sys_emailed (sys_email_id);
CREATE INDEX sys_emailed_org_id ON sys_emailed (org_id);
CREATE INDEX sys_emailed_table_id ON sys_emailed (table_id);
CREATE INDEX sys_emailed_email_type ON sys_emailed (email_type);

CREATE TABLE et_fields (
	et_field_id				serial primary key,
	org_id					integer references orgs,
	et_field_name			varchar(320) not null,
	table_name				varchar(64) not null,
	table_code				integer not null,
	table_link				integer,
	is_active				boolean default true not null
);
CREATE INDEX et_fields_org_id ON et_fields(org_id);
CREATE INDEX et_fields_table_code ON et_fields(table_code);
CREATE INDEX et_fields_table_link ON et_fields(table_link);

CREATE TABLE e_fields (
	e_field_id				serial primary key,
	et_field_id				integer references et_fields,
	org_id					integer references orgs,
	table_code				integer not null,
	table_id				integer,
	e_field_value			varchar(320)
);
CREATE INDEX e_fields_et_field_id ON e_fields(et_field_id);
CREATE INDEX e_fields_table_id ON e_fields(table_id);
CREATE INDEX e_fields_table_code ON e_fields(table_code);
CREATE INDEX e_fields_org_id ON e_fields(org_id);

CREATE TABLE workflows (
	workflow_id				serial primary key,
	source_entity_id		integer not null references entity_types,
	org_id					integer references orgs,
	workflow_name			varchar(240) not null,
	table_name				varchar(64),
	table_link_field		varchar(64),
	table_link_id			integer,
	auto_approve			boolean default false not null,
	approve_email			text not null,
	reject_email			text not null,
	approve_file			varchar(320),
	reject_file				varchar(320),
	link_copy				integer,
	details					text
);
CREATE INDEX workflows_source_entity_id ON workflows (source_entity_id);
CREATE INDEX workflows_org_id ON workflows (org_id);

CREATE TABLE workflow_phases (
	workflow_phase_id		serial primary key,
	workflow_id				integer not null references workflows,
	approval_entity_id		integer not null references entity_types,
	org_id					integer references orgs,
	approval_level			integer default 1 not null,
	return_level			integer default 1 not null,
	escalation_days			integer default 0 not null,
	escalation_hours		integer default 3 not null,
	required_approvals		integer default 1 not null,
	reporting_level			integer default 1 not null,
	use_reporting			boolean default false not null,
	approve_no_reporting	boolean default false not null,
	advice					boolean default false not null,
	notice					boolean default false not null,
	phase_narrative			varchar(240) not null,
	advice_email			text,
	notice_email			text,
	advice_file				varchar(320),
	notice_file				varchar(320),
	details					text
);
CREATE INDEX workflow_phases_workflow_id ON workflow_phases (workflow_id);
CREATE INDEX workflow_phases_approval_entity_id ON workflow_phases (approval_entity_id);
CREATE INDEX workflow_phases_org_id ON workflow_phases (org_id);

CREATE TABLE checklists (
	checklist_id			serial primary key,
	workflow_phase_id		integer not null references workflow_phases,
	org_id					integer references orgs,
	checklist_number		integer,
	manditory				boolean default false not null,
	requirement				text,
	details					text
);
CREATE INDEX checklists_workflow_phase_id ON checklists (workflow_phase_id);
CREATE INDEX checklists_org_id ON checklists (org_id);

CREATE TABLE workflow_sql (
	workflow_sql_id			serial primary key,
	workflow_phase_id		integer not null references workflow_phases,
	org_id					integer references orgs,
	workflow_sql_name		varchar(50),
	is_condition			boolean default false,
	is_action				boolean default false,
	message					text not null,
	sql						text not null
);
CREATE INDEX workflow_sql_workflow_phase_id ON workflow_sql (workflow_phase_id);
CREATE INDEX workflow_sql_org_id ON workflow_sql (org_id);

CREATE TABLE approvals (
	approval_id				serial primary key,
	workflow_phase_id		integer not null references workflow_phases,
	org_entity_id			integer not null references entitys,
	app_entity_id			integer references entitys,
	org_id					integer references orgs,
	approval_level			integer default 1 not null,
	escalation_days			integer default 0 not null,
	escalation_hours		integer default 3 not null,
	escalation_time			timestamp default now() not null,
	forward_id				integer,
	table_name				varchar(64),
	table_id				integer,
	application_date		timestamp default now() not null,
	completion_date			timestamp,
	action_date				timestamp,
	approve_status			varchar(16) default 'Draft' not null,
	approval_narrative		varchar(240),
	to_be_done				text,
	what_is_done			text,
	review_advice			text,
	details					text
);
CREATE INDEX approvals_workflow_phase_id ON approvals (workflow_phase_id);
CREATE INDEX approvals_org_entity_id ON approvals (org_entity_id);
CREATE INDEX approvals_app_entity_id ON approvals (app_entity_id);
CREATE INDEX approvals_org_id ON approvals (org_id);
CREATE INDEX approvals_forward_id ON approvals (forward_id);
CREATE INDEX approvals_table_id ON approvals (table_id);
CREATE INDEX approvals_approve_status ON approvals (approve_status);

CREATE TABLE approval_checklists (
	approval_checklist_id	serial primary key,
	approval_id				integer not null references approvals,
	checklist_id			integer not null references checklists,
	org_id					integer references orgs,
	requirement				text,
	manditory				boolean default false not null,
	done					boolean default false not null,
	narrative				varchar(320)
);
CREATE INDEX approval_checklists_approval_id ON approval_checklists (approval_id);
CREATE INDEX approval_checklists_checklist_id ON approval_checklists (checklist_id);
CREATE INDEX approval_checklists_org_id ON approval_checklists (org_id);

CREATE TABLE approval_lists (
	approval_list_id		serial primary key,
	workflow_id				integer not null references workflows,
	entity_id				integer not null references entitys,
	entered_by				integer references entitys,
	org_id					integer references orgs,
	table_name				varchar(64),
	table_id				integer,
	application_date		timestamp default now() not null,
	action_date				timestamp,
	approve_status			varchar(16) default 'Completed' not null
);
CREATE INDEX approval_lists_workflow_id ON approval_lists (workflow_id);
CREATE INDEX approval_lists_entity_id ON approval_lists (entity_id);
CREATE INDEX approval_lists_entered_by ON approval_lists (entered_by);
CREATE INDEX approval_lists_table_id ON approval_lists (table_id);
CREATE INDEX approval_lists_approve_status ON approval_lists (approve_status);

CREATE TABLE workflow_logs (
	workflow_log_id			serial primary key,
	org_id					integer references orgs,
	table_name				varchar(64),
	table_id				integer,
	table_old_id			integer
);
CREATE INDEX workflow_logs_org_id ON workflow_logs (org_id);

CREATE SEQUENCE workflow_table_id_seq;

CREATE SEQUENCE picture_id_seq;

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

CREATE VIEW vw_sys_emailed AS
	SELECT sys_emails.sys_email_id, sys_emails.sys_email_name, 
		sys_emails.use_type, sys_emails.title, sys_emails.details,
		sys_emailed.sys_emailed_id, sys_emailed.org_id, sys_emailed.table_id, sys_emailed.table_name, 
		sys_emailed.email_type, sys_emailed.created, sys_emailed.emailed, sys_emailed.narrative
	FROM sys_emailed LEFT JOIN sys_emails ON sys_emailed.sys_email_id = sys_emails.sys_email_id;

CREATE VIEW vw_sys_countrys AS
	SELECT sys_continents.sys_continent_id, sys_continents.sys_continent_name,
		sys_countrys.sys_country_id, sys_countrys.sys_country_code, sys_countrys.sys_country_number,
		sys_countrys.sys_phone_code, sys_countrys.sys_country_name
	FROM sys_continents INNER JOIN sys_countrys ON sys_continents.sys_continent_id = sys_countrys.sys_continent_id;

CREATE VIEW vw_address AS
	SELECT sys_countrys.sys_country_id, sys_countrys.sys_country_name, address.address_id, address.org_id, address.address_name,
		address.table_name, address.table_id, address.post_office_box, address.postal_code, address.premises, address.street, address.town,
		address.phone_number, address.extension, address.mobile, address.fax, address.email, address.is_default, address.website, address.details,
		address_types.address_type_id, address_types.address_type_name,
		address.address_name as disp_name
	FROM address INNER JOIN sys_countrys ON address.sys_country_id = sys_countrys.sys_country_id
		LEFT JOIN address_types ON address.address_type_id = address_types.address_type_id;

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

CREATE VIEW vw_entity_address AS
	SELECT vw_address.address_id, vw_address.address_name,
		vw_address.sys_country_id, vw_address.sys_country_name, vw_address.table_id, vw_address.table_name,
		vw_address.is_default, vw_address.post_office_box, vw_address.postal_code, vw_address.premises,
		vw_address.street, vw_address.town, vw_address.phone_number, vw_address.extension, vw_address.mobile,
		vw_address.fax, vw_address.email, vw_address.website, vw_address.disp_name
	FROM vw_address
	WHERE (vw_address.table_name = 'entitys') AND (vw_address.is_default = true);

CREATE VIEW vw_org_select AS
	(SELECT org_id, parent_org_id, org_name
	FROM orgs
	WHERE (is_active = true) AND (org_id <> parent_org_id))
	UNION
	(SELECT org_id, org_id, org_name
	FROM orgs
	WHERE (is_active = true));

CREATE VIEW vw_orgs AS
	SELECT orgs.org_id, orgs.org_name, orgs.is_default, orgs.is_active, orgs.logo,
		orgs.org_full_name, orgs.pin, orgs.pcc, orgs.details,
		currency.currency_id, currency.currency_name, currency.currency_symbol,
		vw_org_address.org_sys_country_id, vw_org_address.org_sys_country_name,
		vw_org_address.org_address_id, vw_org_address.org_table_name,
		vw_org_address.org_post_office_box, vw_org_address.org_postal_code,
		vw_org_address.org_premises, vw_org_address.org_street, vw_org_address.org_town,
		vw_org_address.org_phone_number, vw_org_address.org_extension,
		vw_org_address.org_mobile, vw_org_address.org_fax, vw_org_address.org_email, vw_org_address.org_website
	FROM orgs INNER JOIN currency ON orgs.currency_id = currency.currency_id
		LEFT JOIN vw_org_address ON orgs.org_id = vw_org_address.org_table_id;

CREATE VIEW vw_entity_types AS
	SELECT use_keys.use_key_id, use_keys.use_key_name, use_keys.use_function,
		entity_types.entity_type_id, entity_types.org_id, entity_types.entity_type_name,
		entity_types.entity_role, entity_types.start_view, entity_types.group_email,
		entity_types.description, entity_types.details
	FROM use_keys INNER JOIN entity_types ON use_keys.use_key_id = entity_types.use_key_id;

CREATE VIEW vw_entitys AS
	SELECT vw_orgs.org_id, vw_orgs.org_name, vw_orgs.is_default as org_is_default,
		vw_orgs.is_active as org_is_active, vw_orgs.logo as org_logo,

		vw_orgs.org_sys_country_id, vw_orgs.org_sys_country_name,
		vw_orgs.org_address_id, vw_orgs.org_table_name,
		vw_orgs.org_post_office_box, vw_orgs.org_postal_code,
		vw_orgs.org_premises, vw_orgs.org_street, vw_orgs.org_town,
		vw_orgs.org_phone_number, vw_orgs.org_extension,
		vw_orgs.org_mobile, vw_orgs.org_fax, vw_orgs.org_email, vw_orgs.org_website,

		vw_entity_address.address_id, vw_entity_address.address_name,
		vw_entity_address.sys_country_id, vw_entity_address.sys_country_name, vw_entity_address.table_name,
		vw_entity_address.is_default, vw_entity_address.post_office_box, vw_entity_address.postal_code,
		vw_entity_address.premises, vw_entity_address.street, vw_entity_address.town,
		vw_entity_address.phone_number, vw_entity_address.extension, vw_entity_address.mobile,
		vw_entity_address.fax, vw_entity_address.email, vw_entity_address.website,

		entity_types.entity_type_id, entity_types.entity_type_name, entity_types.entity_role,

		entitys.entity_id, entitys.entity_name, entitys.user_name, entitys.super_user, entitys.entity_leader,
		entitys.date_enroled, entitys.is_active, entitys.entity_password, entitys.first_password,
		entitys.function_role, entitys.use_key_id, entitys.primary_email, entitys.primary_telephone

	FROM (entitys LEFT JOIN vw_entity_address ON entitys.entity_id = vw_entity_address.table_id)
		INNER JOIN vw_orgs ON entitys.org_id = vw_orgs.org_id
		INNER JOIN entity_types ON entitys.entity_type_id = entity_types.entity_type_id;

CREATE VIEW vw_entity_orgs AS
	SELECT entitys.entity_id, entitys.entity_name, entitys.user_name, entitys.super_user, entitys.entity_leader,
		entitys.date_enroled, entitys.is_active, entitys.entity_password, entitys.first_password,
		entitys.function_role, entitys.use_key_id, entitys.primary_email, entitys.primary_telephone,
		orgs.org_id, orgs.org_name, orgs.org_full_name,
		entity_orgs.entity_org_id, entity_orgs.details
	FROM entity_orgs INNER JOIN entitys ON entitys.entity_id = entity_orgs.entity_id
		INNER JOIN orgs ON entity_orgs.org_id = orgs.org_id;

CREATE VIEW vw_entity_values AS
	SELECT entitys.entity_id, entitys.entity_name,
		entity_fields.entity_field_id, entity_fields.entity_field_name,
		entity_values.org_id, entity_values.entity_value_id, entity_values.entity_value
	FROM entity_values INNER JOIN entitys ON entity_values.entity_id = entitys.entity_id
		INNER JOIN entity_fields ON entity_values.entity_field_id = entity_fields.entity_field_id;

CREATE VIEW vw_entity_subscriptions AS
	SELECT entity_types.entity_type_id, entity_types.entity_type_name, entity_types.entity_role,
		entitys.entity_id, entitys.entity_name, entitys.user_name,
		entitys.primary_email, entitys.primary_telephone,
		entity_subscriptions.entity_subscription_id, entity_subscriptions.org_id, entity_subscriptions.details
	FROM entity_subscriptions INNER JOIN entity_types ON entity_subscriptions.entity_type_id = entity_types.entity_type_id
		INNER JOIN entitys ON entity_subscriptions.entity_id = entitys.entity_id;

CREATE VIEW vw_sys_access_entitys AS
	SELECT vw_sys_access_levels.sys_app_id, vw_sys_access_levels.sys_app_name,
		vw_sys_access_levels.sys_app_module_id, vw_sys_access_levels.sys_app_module_name,
		vw_sys_access_levels.sys_access_level_id, vw_sys_access_levels.use_key_id,
		vw_sys_access_levels.sys_access_level_name, vw_sys_access_levels.access_tag,
		entitys.entity_id, entitys.entity_name,
		sys_access_entitys.org_id, sys_access_entitys.sys_access_entity_id,
		sys_access_entitys.narrative
	FROM sys_access_entitys INNER JOIN vw_sys_access_levels ON sys_access_entitys.sys_access_level_id = vw_sys_access_levels.sys_access_level_id
		INNER JOIN entitys ON sys_access_entitys.entity_id = entitys.entity_id;

CREATE VIEW vw_reporting AS
	SELECT entitys.entity_id, entitys.entity_name, rpt.entity_id as rpt_id, rpt.entity_name as rpt_name,
		reporting.org_id, reporting.reporting_id, reporting.date_from,
		reporting.date_to, reporting.primary_report, reporting.is_active, reporting.ps_reporting,
		reporting.reporting_level, reporting.details
	FROM reporting INNER JOIN entitys ON reporting.entity_id = entitys.entity_id
		INNER JOIN entitys as rpt ON reporting.report_to_id = rpt.entity_id;

CREATE VIEW vw_reporting_groups AS
	SELECT reporting.org_id, reporting.reporting_level
	FROM reporting
	GROUP BY reporting.org_id, reporting.reporting_level
	ORDER BY reporting.reporting_level;

CREATE VIEW vw_e_fields AS
	SELECT orgs.org_id, orgs.org_name,
		et_fields.et_field_id, et_fields.et_field_name, et_fields.table_name, et_fields.table_link,
		e_fields.e_field_id, e_fields.table_code, e_fields.table_id, e_fields.e_field_value
	FROM e_fields INNER JOIN orgs ON e_fields.org_id = orgs.org_id
		INNER JOIN et_fields ON e_fields.et_field_id = et_fields.et_field_id;

CREATE VIEW vw_workflows AS
	SELECT entity_types.entity_type_id as source_entity_id, entity_types.entity_type_name as source_entity_name,
		workflows.workflow_id, workflows.org_id, workflows.workflow_name, workflows.table_name, workflows.table_link_field,
		workflows.table_link_id, workflows.approve_email, workflows.reject_email,
		workflows.approve_file, workflows.reject_file, workflows.details
	FROM workflows INNER JOIN entity_types ON workflows.source_entity_id = entity_types.entity_type_id;

CREATE VIEW vw_workflow_phases AS
	SELECT vw_workflows.source_entity_id, vw_workflows.source_entity_name, vw_workflows.workflow_id,
		vw_workflows.workflow_name, vw_workflows.table_name, vw_workflows.table_link_field, vw_workflows.table_link_id,
		vw_workflows.approve_email, vw_workflows.reject_email, vw_workflows.approve_file, vw_workflows.reject_file,
		entity_types.entity_type_id as approval_entity_id, entity_types.entity_type_name as approval_entity_name,
		workflow_phases.workflow_phase_id, workflow_phases.org_id, workflow_phases.approval_level,
		workflow_phases.return_level, workflow_phases.escalation_days, workflow_phases.escalation_hours,
		workflow_phases.notice, workflow_phases.notice_email, workflow_phases.notice_file,
		workflow_phases.advice, workflow_phases.advice_email, workflow_phases.advice_file,
		workflow_phases.required_approvals, workflow_phases.use_reporting, workflow_phases.reporting_level,
		workflow_phases.phase_narrative, workflow_phases.details
	FROM (workflow_phases INNER JOIN vw_workflows ON workflow_phases.workflow_id = vw_workflows.workflow_id)
		INNER JOIN entity_types ON workflow_phases.approval_entity_id = entity_types.entity_type_id;
		
CREATE VIEW vw_approval_lists AS
	SELECT vw_workflows.source_entity_id, vw_workflows.source_entity_name, vw_workflows.workflow_id,
		vw_workflows.workflow_name, vw_workflows.table_link_field, vw_workflows.table_link_id,
		vw_workflows.approve_email, vw_workflows.reject_email, vw_workflows.approve_file, vw_workflows.reject_file,
		en.entity_id, en.entity_name, en.primary_email,
		eb.entity_id as entered_by_id, eb.entity_name as entered_by_name, eb.primary_email as entered_by_email,
		orgs.org_id, orgs.org_name,  
		approval_lists.approval_list_id, approval_lists.table_name, approval_lists.table_id, 
		approval_lists.application_date, approval_lists.action_date, approval_lists.approve_status,
		(vw_workflows.workflow_name || ' ' || approval_lists.approve_status) as workflow_narrative
	FROM approval_lists INNER JOIN vw_workflows ON approval_lists.workflow_id = vw_workflows.workflow_id
		INNER JOIN entitys en ON approval_lists.entity_id = en.entity_id
		INNER JOIN entitys eb ON approval_lists.entered_by = eb.entity_id
		INNER JOIN orgs ON approval_lists.org_id = orgs.org_id;
	
CREATE VIEW vw_workflow_entitys AS
	SELECT vw_workflow_phases.workflow_id, vw_workflow_phases.org_id, vw_workflow_phases.workflow_name, vw_workflow_phases.table_name,
		vw_workflow_phases.table_link_id, vw_workflow_phases.source_entity_id, vw_workflow_phases.source_entity_name,
		vw_workflow_phases.approve_file, vw_workflow_phases.reject_file,
		vw_workflow_phases.approval_entity_id, vw_workflow_phases.approval_entity_name,
		vw_workflow_phases.workflow_phase_id, vw_workflow_phases.approval_level,
		vw_workflow_phases.return_level, vw_workflow_phases.escalation_days, vw_workflow_phases.escalation_hours,
		vw_workflow_phases.notice, vw_workflow_phases.notice_email, vw_workflow_phases.notice_file,
		vw_workflow_phases.advice, vw_workflow_phases.advice_email, vw_workflow_phases.advice_file,
		vw_workflow_phases.required_approvals, vw_workflow_phases.use_reporting, vw_workflow_phases.phase_narrative,
		entity_subscriptions.entity_subscription_id, entity_subscriptions.entity_id
	FROM vw_workflow_phases INNER JOIN entity_subscriptions ON vw_workflow_phases.source_entity_id = entity_subscriptions.entity_type_id;

CREATE VIEW vw_approvals AS
	SELECT vw_workflow_phases.workflow_id, vw_workflow_phases.workflow_name,
		vw_workflow_phases.approve_email, vw_workflow_phases.reject_email,
		vw_workflow_phases.source_entity_id, vw_workflow_phases.source_entity_name,
		vw_workflow_phases.approval_entity_id, vw_workflow_phases.approval_entity_name,
		vw_workflow_phases.approve_file, vw_workflow_phases.reject_file,
		vw_workflow_phases.workflow_phase_id, vw_workflow_phases.approval_level, vw_workflow_phases.phase_narrative,
		vw_workflow_phases.return_level, vw_workflow_phases.required_approvals,
		vw_workflow_phases.notice, vw_workflow_phases.notice_email, vw_workflow_phases.notice_file,
		vw_workflow_phases.advice, vw_workflow_phases.advice_email, vw_workflow_phases.advice_file,
		vw_workflow_phases.use_reporting,
		approvals.approval_id, approvals.org_id, approvals.forward_id, approvals.table_name, approvals.table_id,
		approvals.completion_date, approvals.escalation_days, approvals.escalation_hours,
		approvals.escalation_time, approvals.application_date, approvals.approve_status, approvals.action_date,
		approvals.approval_narrative, approvals.to_be_done, approvals.what_is_done, approvals.review_advice, approvals.details,
		oe.entity_id as org_entity_id, oe.entity_name as org_entity_name, oe.user_name as org_user_name, oe.primary_email as org_primary_email,
		ae.entity_id as app_entity_id, ae.entity_name as app_entity_name, ae.user_name as app_user_name, ae.primary_email as app_primary_email,
		(CASE WHEN approvals.approve_status = 'Draft' THEN 1 ELSE 0 END) as is_draft,
		(CASE WHEN approvals.approve_status = 'Completed' THEN 1 ELSE 0 END) as is_completed,
		(CASE WHEN approvals.approve_status = 'Approved' THEN 1 ELSE 0 END) as is_approved,
		(CASE WHEN approvals.approve_status = 'Rejected' THEN 1 ELSE 0 END) as is_rejected
	FROM (vw_workflow_phases INNER JOIN approvals ON vw_workflow_phases.workflow_phase_id = approvals.workflow_phase_id)
		INNER JOIN entitys as oe ON approvals.org_entity_id = oe.entity_id
		LEFT JOIN entitys as ae ON approvals.app_entity_id = ae.entity_id;
		
CREATE VIEW vw_approval_status AS
	SELECT vw_approvals.org_id, vw_approvals.workflow_id, vw_approvals.workflow_name,
		vw_approvals.approve_email, vw_approvals.reject_email,
		vw_approvals.source_entity_id, vw_approvals.source_entity_name,
		vw_approvals.approve_file, vw_approvals.reject_file,
		vw_approvals.table_name, vw_approvals.table_id,
		vw_approvals.org_entity_id, vw_approvals.org_entity_name, vw_approvals.org_user_name, vw_approvals.org_primary_email,
		sum(is_draft) as count_draft, sum(is_completed) as count_completed, sum(is_approved) as count_approved, sum(is_rejected) as count_rejected,
		(CASE WHEN sum(is_rejected) = 0 THEN vw_approvals.workflow_name || ' Approved'
			ELSE vw_approvals.workflow_name || ' declined' END) as workflow_narrative
	FROM vw_approvals
	GROUP BY vw_approvals.org_id, vw_approvals.workflow_id, vw_approvals.workflow_name,
		vw_approvals.approve_email, vw_approvals.reject_email,
		vw_approvals.source_entity_id, vw_approvals.source_entity_name,
		vw_approvals.approve_file, vw_approvals.reject_file,
		vw_approvals.table_name, vw_approvals.table_id,
		vw_approvals.org_entity_id, vw_approvals.org_entity_name, vw_approvals.org_user_name, vw_approvals.org_primary_email;

CREATE VIEW vw_workflow_approvals AS
	SELECT vw_approvals.org_id, vw_approvals.approval_id,
		vw_approvals.workflow_id, vw_approvals.workflow_name, vw_approvals.approve_email,
		vw_approvals.approve_file, vw_approvals.reject_file,
		vw_approvals.reject_email, vw_approvals.source_entity_id, vw_approvals.source_entity_name,
		vw_approvals.table_name, vw_approvals.table_id, vw_approvals.org_entity_id,
		vw_approvals.org_entity_name, vw_approvals.org_user_name,
		vw_approvals.org_primary_email, rt.rejected_count, vw_approvals.approve_status,
		(CASE WHEN rt.rejected_count is null THEN vw_approvals.workflow_name || ' Approved'
			ELSE vw_approvals.workflow_name || ' declined' END) as workflow_narrative
	FROM vw_approvals LEFT JOIN
		(SELECT table_id, count(approval_id) as rejected_count FROM approvals WHERE (approve_status = 'Rejected') AND (approvals.forward_id is null)
		GROUP BY table_id) as rt ON vw_approvals.table_id = rt.table_id
	GROUP BY vw_approvals.org_id, vw_approvals.approval_id,
		vw_approvals.workflow_id, vw_approvals.workflow_name, vw_approvals.approve_email,
		vw_approvals.approve_file, vw_approvals.reject_file,
		vw_approvals.reject_email, vw_approvals.source_entity_id, vw_approvals.source_entity_name,
		vw_approvals.table_name, vw_approvals.table_id, vw_approvals.org_entity_id,
		vw_approvals.org_entity_name, vw_approvals.org_user_name,
		vw_approvals.org_primary_email, rt.rejected_count, vw_approvals.approve_status;

CREATE VIEW vw_approvals_entitys AS
	(SELECT vw_workflow_phases.workflow_id, vw_workflow_phases.workflow_name,
		vw_workflow_phases.source_entity_id, vw_workflow_phases.source_entity_name,
		vw_workflow_phases.approval_entity_id, vw_workflow_phases.approval_entity_name,
		vw_workflow_phases.workflow_phase_id, vw_workflow_phases.approval_level,
		vw_workflow_phases.notice, vw_workflow_phases.notice_email, vw_workflow_phases.notice_file,
		vw_workflow_phases.advice, vw_workflow_phases.advice_email, vw_workflow_phases.advice_file,
		vw_workflow_phases.return_level, vw_workflow_phases.required_approvals, vw_workflow_phases.phase_narrative,
		vw_workflow_phases.use_reporting,
		approvals.approval_id, approvals.org_id, approvals.forward_id, approvals.table_name, approvals.table_id,
		approvals.completion_date, approvals.escalation_days, approvals.escalation_hours,
		approvals.escalation_time, approvals.application_date, approvals.approve_status, approvals.action_date,
		approvals.approval_narrative, approvals.to_be_done, approvals.what_is_done, approvals.review_advice, approvals.details,
		oe.entity_id as org_entity_id, oe.entity_name as org_entity_name, oe.user_name as org_user_name, oe.primary_email as org_primary_email,
		entitys.entity_id, entitys.entity_name, entitys.user_name, entitys.primary_email
	FROM ((vw_workflow_phases INNER JOIN approvals ON vw_workflow_phases.workflow_phase_id = approvals.workflow_phase_id)
		INNER JOIN entitys as oe  ON approvals.org_entity_id = oe.entity_id)
		INNER JOIN entity_subscriptions ON vw_workflow_phases.approval_entity_id = entity_subscriptions.entity_type_id
		INNER JOIN entitys ON entity_subscriptions.entity_id = entitys.entity_id
	WHERE (approvals.forward_id is null) AND (vw_workflow_phases.use_reporting = false))
	UNION
	(SELECT vw_workflow_phases.workflow_id, vw_workflow_phases.workflow_name,
		vw_workflow_phases.source_entity_id, vw_workflow_phases.source_entity_name,
		vw_workflow_phases.approval_entity_id, vw_workflow_phases.approval_entity_name,
		vw_workflow_phases.workflow_phase_id, vw_workflow_phases.approval_level,
		vw_workflow_phases.notice, vw_workflow_phases.notice_email, vw_workflow_phases.notice_file,
		vw_workflow_phases.advice, vw_workflow_phases.advice_email, vw_workflow_phases.advice_file,
		vw_workflow_phases.return_level, vw_workflow_phases.required_approvals, vw_workflow_phases.phase_narrative,
		vw_workflow_phases.use_reporting,
		approvals.approval_id, approvals.org_id, approvals.forward_id, approvals.table_name, approvals.table_id,
		approvals.completion_date, approvals.escalation_days, approvals.escalation_hours,
		approvals.escalation_time, approvals.application_date, approvals.approve_status, approvals.action_date,
		approvals.approval_narrative, approvals.to_be_done, approvals.what_is_done, approvals.review_advice, approvals.details,
		oe.entity_id as org_entity_id, oe.entity_name as org_entity_name, oe.user_name as org_user_name, oe.primary_email as org_primary_email,
		entitys.entity_id, entitys.entity_name, entitys.user_name, entitys.primary_email
	FROM ((vw_workflow_phases INNER JOIN approvals ON vw_workflow_phases.workflow_phase_id = approvals.workflow_phase_id)
		INNER JOIN entitys as oe  ON approvals.org_entity_id = oe.entity_id)
		INNER JOIN reporting ON ((approvals.org_entity_id = reporting.entity_id)
			AND (vw_workflow_phases.reporting_level = reporting.reporting_level))
		INNER JOIN entitys ON reporting.report_to_id = entitys.entity_id
	WHERE (approvals.forward_id is null) AND (reporting.primary_report = true) AND (reporting.is_active = true)
		AND (vw_workflow_phases.use_reporting = true));

CREATE VIEW vw_workflow_sql AS
	SELECT workflow_sql.org_id, workflow_sql.workflow_sql_id, workflow_sql.workflow_phase_id, workflow_sql.workflow_sql_name,
		workflow_sql.is_condition, workflow_sql.is_action, workflow_sql.message, workflow_sql.sql,
		approvals.approval_id, approvals.org_entity_id, approvals.app_entity_id,
		approvals.approval_level, approvals.escalation_days, approvals.escalation_hours, approvals.escalation_time,
		approvals.forward_id, approvals.table_name, approvals.table_id, approvals.application_date, approvals.completion_date,
		approvals.action_date, approvals.approve_status, approvals.approval_narrative
	FROM workflow_sql INNER JOIN approvals ON workflow_sql.workflow_phase_id = approvals.workflow_phase_id;

CREATE VIEW tomcat_users AS
	SELECT entitys.user_name, entitys.entity_password, entity_types.entity_role
	FROM (entity_subscriptions INNER JOIN entitys ON entity_subscriptions.entity_id = entitys.entity_id)
		INNER JOIN entity_types ON entity_subscriptions.entity_type_id = entity_types.entity_type_id
	WHERE entitys.is_active = true;
	
CREATE VIEW vw_sys_logins AS
	SELECT entitys.entity_id, entitys.entity_name, 
		orgs.org_id, orgs.org_name,
		sys_apps.sys_app_id, sys_apps.sys_app_name, 
		sys_logins.sys_login_id, sys_logins.login_time, sys_logins.login_ip, 
		sys_logins.phone_serial_number, sys_logins.correct_login, sys_logins.narrative
	FROM sys_logins INNER JOIN entitys ON sys_logins.entity_id = entitys.entity_id
		INNER JOIN orgs ON entitys.org_id = orgs.org_id
		LEFT JOIN sys_apps ON sys_logins.sys_app_id = sys_apps.sys_app_id;
			
CREATE VIEW vws_sys_logins AS
	SELECT entitys.entity_id, entitys.entity_name,
		orgs.org_id, orgs.org_name,
		sys_apps.sys_app_id, sys_apps.sys_app_name, 
		count(sys_logins.sys_login_id) as no_of_logins
	FROM sys_logins INNER JOIN entitys ON sys_logins.entity_id = entitys.entity_id
		INNER JOIN orgs ON entitys.org_id = orgs.org_id
		LEFT JOIN sys_apps ON sys_logins.sys_app_id = sys_apps.sys_app_id
	GROUP BY entitys.entity_id, entitys.entity_name,
		orgs.org_id, orgs.org_name,
		sys_apps.sys_app_id, sys_apps.sys_app_name;

CREATE VIEW vws_app_logins AS
	SELECT org_id, org_name, sys_app_name, 
		count(entity_id) no_users, sum(no_of_logins) as no_logins
	FROM vws_sys_logins
	GROUP BY org_id, org_name, sys_app_name
	ORDER BY org_name, sys_app_name;
	
CREATE VIEW select_yes_no AS
	SELECT bs.column1 as select_id, bs.column2 as select_state, bs.column3 as select_value
	FROM (VALUES (0, false, 'No'), (1, true, 'Yes')) bs;
	
CREATE OR REPLACE FUNCTION get_org_name(integer) RETURNS varchar(50) AS $$
	SELECT orgs.org_name
	FROM orgs WHERE (orgs.org_id = $1);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_org_full_name(integer) RETURNS varchar(120) AS $$
	SELECT orgs.org_full_name
	FROM orgs WHERE (orgs.org_id = $1);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_org_logo(integer) RETURNS varchar(50) AS $$
	SELECT orgs.logo
	FROM orgs WHERE (orgs.org_id = $1);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_entity_name(integer) RETURNS varchar(50) AS $$
	SELECT entitys.entity_name
	FROM entitys WHERE (entitys.entity_id = $1);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_entity_type_id(integer, integer) RETURNS int AS $$
	SELECT max(entity_type_id)
	FROM entity_types 
	WHERE (org_id = $1) AND (use_key_id = $2);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_org_letter_head(integer) RETURNS varchar(50) AS $$
	SELECT orgs.letter_head
	FROM orgs WHERE (orgs.org_id = $1);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION default_currency(varchar(16)) RETURNS integer AS $$
	SELECT orgs.currency_id
	FROM orgs INNER JOIN entitys ON orgs.org_id = entitys.org_id
	WHERE (entitys.entity_id = $1::int);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_sys_email_id(int, int) RETURNS integer AS $$
	SELECT max(sys_email_id)
	FROM sys_emails WHERE (org_id = $1) AND (use_type = $2);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_address(varchar(32), int) RETURNS text AS $$
	SELECT COALESCE(premises || E'\n', '') || 
		COALESCE(street || E'\n', '') || 
		COALESCE(town || ' - ', '') ||
		COALESCE(postal_code, '') ||
		COALESCE(E'\n' || phone_number, '') ||
		COALESCE(E'\n' || email, '')
	FROM address
	WHERE (is_default = true)
		AND (table_name = $1) AND (table_id = $2)
	LIMIT 1;
$$ LANGUAGE SQL;

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

CREATE OR REPLACE FUNCTION ins_address() RETURNS trigger AS $$
DECLARE
	v_address_id		integer;
BEGIN
	SELECT address_id INTO v_address_id
	FROM address WHERE (is_default = true)
		AND (table_name = NEW.table_name) AND (table_id = NEW.table_id) AND (address_id <> NEW.address_id);

	IF(NEW.is_default is null)THEN
		NEW.is_default := false;
	END IF;

	IF(NEW.is_default = true) AND (v_address_id is not null) THEN
		RAISE EXCEPTION 'Only one default Address allowed.';
	ELSIF (NEW.is_default = false) AND (v_address_id is null) THEN
		NEW.is_default := true;
	END IF;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ins_address BEFORE INSERT OR UPDATE ON address
    FOR EACH ROW EXECUTE PROCEDURE ins_address();

CREATE OR REPLACE FUNCTION first_password() RETURNS varchar(12) AS $$
DECLARE
	rnd integer;
	passchange varchar(12);
BEGIN
	passchange := trunc(random()*1000);
	rnd := trunc(65+random()*25);
	passchange := passchange || chr(rnd);
	passchange := passchange || trunc(random()*1000);
	rnd := trunc(65+random()*25);
	passchange := passchange || chr(rnd);
	rnd := trunc(65+random()*25);
	passchange := passchange || chr(rnd);

	RETURN passchange;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION email_credentials(varchar(12), varchar(32), varchar(32)) RETURNS varchar(120) AS $$
DECLARE
	v_entity_id			integer;
	v_org_id			integer;
	v_sys_email_id		integer;
	msg					varchar(120);
BEGIN
	SELECT entity_id, org_id INTO v_entity_id, v_org_id
	FROM entitys WHERE (entity_id = $1::int);

	SELECT sys_email_id INTO v_sys_email_id
	FROM sys_emails WHERE (use_type = 2) AND (org_id = v_org_id);

	IF(v_sys_email_id is null)THEN
		msg := 'Ensure you have an email template setup';
	ELSE
		INSERT INTO sys_emailed (org_id, sys_email_id, table_id, table_name)
		VALUES(v_org_id, v_sys_email_id, v_entity_id, 'entitys');

		msg := 'Emailed the credentials';
	END IF;

	RETURN msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION email_to_username(varchar(12), varchar(32), varchar(32)) RETURNS varchar(120) AS $$
DECLARE
	v_entity_id			integer;
	v_email				varchar(120);
	v_user_name			varchar(120);
	msg					varchar(120);
BEGIN
	msg := 'Error changing email to username';

	SELECT entity_id, trim(lower(primary_email)) INTO v_entity_id, v_email
	FROM entitys WHERE (entity_id = $1::int);

	SELECT user_name INTO v_user_name
	FROM entitys WHERE (trim(lower(user_name)) = v_email);

	IF(v_email is null)THEN
		msg := 'Ensure you have an email entered';
	ELSIF(v_user_name is not null)THEN
		msg := 'There is an existing user with that email address as username';
	ELSE
		UPDATE entitys SET user_name = v_email WHERE entity_id = v_entity_id;
		msg := 'Email address updated to username';
	END IF;

	RETURN msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION change_password(varchar(12), varchar(32), varchar(32)) RETURNS varchar(120) AS $$
DECLARE
	old_password 			varchar(64);
	v_entity_id				integer;
	v_pass_change			varchar(120);
	msg						varchar(120);
BEGIN
	msg := 'Password Error';
	v_entity_id := $1::int;

	SELECT Entity_password INTO old_password
	FROM entitys WHERE (entity_id = v_entity_id);

	IF ($2 = '0') THEN
		v_pass_change := first_password();
		UPDATE entitys SET first_password = v_pass_change, Entity_password = md5(v_pass_change)
		WHERE (entity_id = v_entity_id);
		msg := 'New Password Changed';
	ELSIF (old_password = md5($2)) THEN
		UPDATE entitys SET Entity_password = md5($3) WHERE (entity_id = v_entity_id);
		msg := 'Password Changed';
	END IF;

	RETURN msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION password_check(varchar(12)) RETURNS integer AS $$
DECLARE
	v_org_id				integer;
	v_password_scheme		integer;
	v_entity_id				integer;
	v_password_okay			integer;
BEGIN

	SELECT org_id INTO v_org_id
	FROM entitys WHERE (entity_id = $1::int);

	SELECT password_scheme INTO v_password_scheme
	FROM orgs WHERE (org_id = v_org_id);

	v_password_okay := 1;

	IF (v_password_scheme = 2) THEN		---- Must change password
		SELECT entity_id INTO v_entity_id
		FROM entitys WHERE (entity_id = $1::int)
		AND (first_password = md5(entity_password));

		IF (v_entity_id is not null) THEN
			v_password_okay := 2;
		END IF;
	END IF;

	RETURN v_password_okay;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION change_password(varchar(12), varchar(32), varchar(32), varchar(32)) RETURNS varchar(120) AS $$
DECLARE
	old_password 		varchar(64);
	v_entity_id			integer;
	v_pass_change		varchar(120);
	msg					varchar(120);
BEGIN
	msg := 'Password Error';
	v_entity_id := $1::int;

	SELECT Entity_password INTO old_password
	FROM entitys WHERE (entity_id = v_entity_id);

	IF ($3 = '1') THEN
		v_pass_change := first_password();
		UPDATE entitys SET first_password = v_pass_change, Entity_password = md5(v_pass_change)
		WHERE (entity_id = v_entity_id);
		msg := 'Password Changed';
	END IF;

	RETURN msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ins_password() RETURNS trigger AS $$
DECLARE
	v_entity_id		integer;
BEGIN

	SELECT entity_id INTO v_entity_id
	FROM entitys
	WHERE (trim(lower(user_name)) = trim(lower(NEW.user_name)))
		AND entity_id <> NEW.entity_id;

	IF(v_entity_id is not null)THEN
		RAISE EXCEPTION 'The username exists use a different one or reset password for the current one';
	END IF;

	IF(TG_OP = 'INSERT') THEN
		IF(NEW.first_password is null)THEN
			NEW.first_password := first_password();
		END IF;

		IF (NEW.entity_password is null) THEN
			NEW.entity_password := md5(NEW.first_password);
		END IF;
	ELSIF(OLD.first_password <> NEW.first_password) THEN
		NEW.Entity_password := md5(NEW.first_password);
	END IF;

	IF(NEW.user_name is null)THEN
		SELECT lower(trim(org_sufix)) || '.' || lower(trim(replace(NEW.entity_name, ' ', ''))) INTO NEW.user_name
		FROM orgs
		WHERE org_id = NEW.org_id;
	END IF;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ins_password BEFORE INSERT OR UPDATE ON entitys
	FOR EACH ROW EXECUTE PROCEDURE ins_password();

CREATE OR REPLACE FUNCTION ins_entitys() RETURNS trigger AS $$
BEGIN

	IF(NEW.org_id is null)THEN
		RAISE EXCEPTION 'You have to select a valid organisation';
	END IF;

	SELECT use_key_id INTO NEW.use_key_id
	FROM entity_types
	WHERE (entity_type_id = NEW.entity_type_id);

	IF(NEW.sys_language_id is null)THEN
		NEW.sys_language_id := 0;
	END IF;
	
	IF(NEW.entity_tag is null)THEN
		NEW.entity_tag := LPAD(NEW.entity_id::text, 5, '0');
	END IF;
	
	NEW.entity_name := trim(NEW.entity_name);

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ins_entitys BEFORE INSERT OR UPDATE ON entitys
	FOR EACH ROW EXECUTE PROCEDURE ins_entitys();

CREATE OR REPLACE FUNCTION aft_entitys() RETURNS trigger AS $$
BEGIN
	IF(NEW.entity_type_id is not null) THEN
		INSERT INTO entity_subscriptions (org_id, entity_type_id, entity_id)
		VALUES (NEW.org_id, NEW.entity_type_id, NEW.entity_id);
	END IF;

	INSERT INTO entity_values (org_id, entity_id, entity_field_id)
	SELECT NEW.org_id, NEW.entity_id, entity_field_id
	FROM entity_fields
	WHERE (org_id = NEW.org_id) AND (is_active = true);

	INSERT INTO sys_access_entitys (entity_id, sys_access_level_id, org_id)
	SELECT NEW.entity_id, sys_access_level_id, org_id
	FROM sys_access_levels
	WHERE (org_id = NEW.org_id) AND (use_key_id = NEW.use_key_id);

	RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER aft_entitys AFTER INSERT ON entitys
	FOR EACH ROW EXECUTE PROCEDURE aft_entitys();

CREATE OR REPLACE FUNCTION upd_access_level(varchar(12), varchar(12), varchar(12), varchar(12)) RETURNS varchar(120) AS $$
DECLARE
	v_entity_id				integer;
	v_org_id				integer;
	v_sys_access_entity_id	integer;
	msg						varchar(120);
BEGIN

	IF($3 = '1')THEN
		SELECT org_id INTO v_org_id FROM entitys WHERE (entity_id = $4::int);

		SELECT sys_access_entity_id INTO v_sys_access_entity_id
		FROM sys_access_entitys
		WHERE (entity_id = $4::int) AND (sys_access_level_id = $1::int);

		IF(v_sys_access_entity_id is null)THEN
			INSERT INTO sys_access_entitys (entity_id, sys_access_level_id, org_id)
			VALUES ($4::int, $1::int, v_org_id);

			msg := 'Granted access level';
		ELSE
			msg := 'Access level already granted';
		END IF;
	ELSIF($3 = '2')THEN
		DELETE FROM sys_access_entitys WHERE sys_access_entity_id = $1::int;

		msg := 'Revoked access level';
		
	ELSIF($3 = '3')THEN
		INSERT INTO sys_access_entitys (entity_id, sys_access_level_id, org_id)
		SELECT employees.entity_id, $1::int,  employees.org_id
		FROM employees LEFT JOIN 
		(SELECT entity_id, sys_access_level_id FROM sys_access_entitys 
			WHERE sys_access_level_id = $1::int) as eal
		ON employees.entity_id = eal.entity_id
		WHERE (employees.active = true) AND (eal.sys_access_level_id is null);
		
		msg := 'Added access level to all active staff';
	ELSIF($3 = '4')THEN
		SELECT entity_id, org_id INTO v_entity_id, v_org_id 
		FROM entitys WHERE (entity_id = $1::int);
		
		INSERT INTO sys_access_entitys (entity_id, org_id, sys_access_level_id)
		SELECT v_entity_id, v_org_id, sys_access_levels.sys_access_level_id
		FROM sys_access_levels LEFT JOIN 
			(SELECT sys_access_level_id FROM sys_access_entitys WHERE entity_id = v_entity_id) ea 
			ON sys_access_levels.sys_access_level_id = ea.sys_access_level_id
		WHERE (sys_access_levels.org_id = v_org_id) AND (ea.sys_access_level_id is null);
		
		msg := 'Added all access rights to user';
	ELSIF($3 = '5')THEN
		DELETE FROM sys_access_entitys
		WHERE (sys_access_level_id = $1::int);

		msg := 'Removed access level to all active staff';
	END IF;

	RETURN msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION add_sys_reset(varchar(120), varchar(120), varchar(64)) RETURNS varchar(120) AS $$
DECLARE
	v_entity_id			integer;
	v_org_id			integer;
	v_msg				varchar(120);
BEGIN

	SELECT entity_id, org_id INTO v_entity_id, v_org_id
	FROM entitys
	WHERE (lower(trim(primary_email)) = lower(trim($1)));

	IF(v_entity_id is null) THEN
		v_msg := 'Email not found';
	ELSE
		INSERT INTO sys_reset (entity_id, org_id, request_email, login_ip)
		VALUES (v_entity_id, v_org_id, $1, $3);

		v_msg := 'The password is being reset';
	END IF;

	return v_msg;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION ins_sys_reset() RETURNS trigger AS $$
DECLARE
	v_sys_email_id		integer;
	v_password			varchar(32);
BEGIN
	SELECT entity_id, org_id INTO NEW.entity_id, NEW.org_id
	FROM entitys
	WHERE (lower(trim(primary_email)) = lower(trim(NEW.request_email)));

	IF(NEW.entity_id is not null) THEN
		v_password := upper(substring(md5(random()::text) from 3 for 9));

		UPDATE entitys SET first_password = v_password, entity_password = md5(v_password)
		WHERE entity_id = NEW.entity_id;

		SELECT sys_email_id INTO v_sys_email_id
		FROM sys_emails WHERE (use_type = 3) AND (org_id = NEW.org_id);

		INSERT INTO sys_emailed (org_id, sys_email_id, table_id, table_name)
		VALUES(NEW.org_id, v_sys_email_id, NEW.entity_id, 'entitys');
	END IF;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ins_sys_reset BEFORE INSERT ON sys_reset
	FOR EACH ROW EXECUTE PROCEDURE ins_sys_reset();

CREATE OR REPLACE FUNCTION password_validate(varchar(64), varchar(32)) RETURNS integer AS $$
DECLARE
	v_entity_id			integer;
	v_entity_password	varchar(64);
BEGIN

	SELECT entity_id, entity_password INTO v_entity_id, v_entity_password
	FROM entitys WHERE (user_name = $1);

	IF(v_entity_id is null)THEN
		v_entity_id = -1;
	ELSIF(md5($2) != v_entity_password) THEN
		v_entity_id = -1;
	END IF;

	return v_entity_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION password_validate(varchar(64), varchar(32), varchar(32), varchar(32)) RETURNS integer AS $$
DECLARE
	v_entity_id			integer;
	v_entity_password	varchar(64);
BEGIN

	SELECT entity_id, entity_password INTO v_entity_id, v_entity_password
	FROM entitys WHERE (user_name = $1);

	IF(v_entity_id is null)THEN
		v_entity_id = -1;
	ELSIF(md5($2) != v_entity_password) THEN
		INSERT INTO sys_logins (entity_id, login_ip, phone_serial_number, correct_login)
		VALUES (v_entity_id, $3, $4, false);
		v_entity_id = -1;
	ELSE
		INSERT INTO sys_logins (entity_id, login_ip, phone_serial_number, correct_login)
		VALUES (v_entity_id, $3, $4, true);
	END IF;

	return v_entity_id;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION Emailed(integer, varchar(64)) RETURNS void AS $$
	UPDATE sys_emailed SET emailed = true WHERE (sys_emailed_id = $2::int);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_et_field_name(integer) RETURNS varchar(120) AS $$
	SELECT et_field_name
	FROM et_fields WHERE (et_field_id = $1);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION add_sys_login(varchar(120)) RETURNS integer AS $$
DECLARE
	v_sys_login_id			integer;
	v_entity_id				integer;
BEGIN
	SELECT entity_id INTO v_entity_id
	FROM entitys WHERE user_name = $1;

	v_sys_login_id := nextval('sys_logins_sys_login_id_seq');

	INSERT INTO sys_logins (sys_login_id, entity_id)
	VALUES (v_sys_login_id, v_entity_id);

	UPDATE entitys SET last_login = current_timestamp
	WHERE (entity_id = v_entity_id);

	return v_sys_login_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION err_sys_login(varchar(120), varchar(120)) RETURNS varchar(120) AS $$
BEGIN
	INSERT INTO sys_err_logins (user_name, login_ip)
	VALUES ($1, $2);

	return 'done';
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION add_sys_login(varchar(64), varchar(64), int, boolean) RETURNS integer AS $$
DECLARE
	v_sys_login_id			integer;
	v_entity_id				integer;
BEGIN
	SELECT entity_id INTO v_entity_id
	FROM entitys WHERE user_name = $1;
	
	IF($4 = true)THEN
		SELECT sys_login_id INTO v_sys_login_id
		FROM sys_logins
		WHERE (login_time::date = current_date) AND (entity_id = v_entity_id)
			AND (sys_app_id = $3);
	END IF;
	
	IF((v_entity_id is not null) AND (v_sys_login_id is null))THEN
		v_sys_login_id := nextval('sys_logins_sys_login_id_seq');

		INSERT INTO sys_logins (sys_login_id, entity_id, login_ip, sys_app_id)
		VALUES (v_sys_login_id, v_entity_id, $2, $3);
	END IF;

	UPDATE entitys SET last_login = current_timestamp
	WHERE (entity_id = v_entity_id);

	return v_sys_login_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION upd_action() RETURNS trigger AS $$
DECLARE
	v_column_name			varchar;
	v_workflow_narrative	varchar(240);
	v_entered_by			integer;
	v_entity_id				integer;
	wfid					integer;
	reca					RECORD;
	recb					RECORD;
	tbid					integer;
	iswf					boolean;
	add_flow				boolean;
	v_reporting_count		integer;
	v_min_level				integer;
BEGIN
	add_flow := false;
	IF(TG_OP = 'INSERT')THEN
		IF (NEW.approve_status = 'Completed')THEN
			add_flow := true;
		END IF;
	ELSE
		IF(OLD.approve_status = 'Draft') AND (NEW.approve_status = 'Completed')THEN
			add_flow := true;
		END IF;
	END IF;

	IF(add_flow = true)THEN
		wfid := nextval('workflow_table_id_seq');
		NEW.workflow_table_id := wfid;
		v_entity_id := NEW.entity_id;

		SELECT column_name INTO v_column_name
		FROM information_schema.columns
		WHERE table_name = TG_TABLE_NAME AND column_name = 'workflow_narrative';
		IF(v_column_name is not null)THEN v_workflow_narrative := NEW.workflow_narrative; ELSE v_workflow_narrative := ''; END IF;

		SELECT column_name INTO v_column_name
		FROM information_schema.columns
		WHERE table_name = TG_TABLE_NAME AND column_name = 'entered_by';
		IF(v_column_name is not null)THEN v_entered_by := NEW.entered_by; ELSE v_entered_by := NEW.entity_id; END IF;
		IF(v_column_name is not null)THEN
			IF(v_entity_id is null)THEN v_entity_id := NEW.entered_by; END IF;
		END IF;

		IF(TG_OP = 'UPDATE')THEN
			IF(OLD.workflow_table_id is not null)THEN
				INSERT INTO workflow_logs (org_id, table_name, table_id, table_old_id)
				VALUES (NEW.org_id, TG_TABLE_NAME, wfid, OLD.workflow_table_id);
			END IF;
		END IF;

		FOR reca IN SELECT workflows.workflow_id, workflows.table_name, workflows.table_link_field,
			workflows.table_link_id, workflows.org_id, workflows.auto_approve
		FROM workflows INNER JOIN entity_subscriptions ON workflows.source_entity_id = entity_subscriptions.entity_type_id
		WHERE (workflows.table_name = TG_TABLE_NAME) AND (entity_subscriptions.entity_id = v_entity_id) LOOP
			iswf := true;
			IF(reca.table_link_field is null)THEN
				iswf := true;
			ELSE
				IF(TG_TABLE_NAME = 'entry_forms')THEN
					tbid := NEW.form_id;
				ELSIF(TG_TABLE_NAME = 'employee_leave')THEN
					tbid := NEW.leave_type_id;
				END IF;
				IF(tbid = reca.table_link_id)THEN
					iswf := true;
				END IF;
			END IF;

			IF(reca.auto_approve = true)THEN
				NEW.approve_status := 'Approved';
				iswf := false;
			END IF;

			IF(iswf = true)THEN
				INSERT INTO approval_lists (workflow_id, entity_id, entered_by, org_id, table_name, table_id, approve_status)
				VALUES (reca.workflow_id, v_entity_id, v_entered_by, reca.org_id, TG_TABLE_NAME, wfid, 'Completed');

				INSERT INTO approvals (org_id, workflow_phase_id, table_name, table_id, org_entity_id,
					escalation_days, escalation_hours, approval_level,
					approval_narrative, to_be_done)
				SELECT org_id, workflow_phase_id, TG_TABLE_NAME, wfid, v_entity_id,
					escalation_days, escalation_hours, approval_level,
					(CASE WHEN phase_narrative is null THEN v_workflow_narrative
						ELSE phase_narrative || ' - ' || v_workflow_narrative END),
					'Approve - ' || COALESCE(phase_narrative, '')
				FROM vw_workflow_entitys
				WHERE (table_name = TG_TABLE_NAME) AND (entity_id = v_entity_id) AND (workflow_id = reca.workflow_id)
				ORDER BY approval_level, workflow_phase_id;

				UPDATE approvals SET approve_status = 'Completed'
				WHERE (table_id = wfid) AND (approval_level = 1);

				SELECT approvals.org_id, approvals.approval_id, approvals.org_id, approvals.table_name, approvals.table_id,
					approvals.approval_level, approvals.review_advice, approvals.org_entity_id,
					workflow_phases.workflow_phase_id, workflow_phases.workflow_id, workflow_phases.return_level,
					workflow_phases.reporting_level, workflow_phases.use_reporting, workflow_phases.approve_no_reporting
				INTO recb
				FROM approvals INNER JOIN workflow_phases ON approvals.workflow_phase_id = workflow_phases.workflow_phase_id
				WHERE (approvals.table_id = wfid) AND (workflow_phases.approval_level = 1);

				SELECT count(reporting_id) INTO v_reporting_count
				FROM reporting
				WHERE (entity_id = recb.org_entity_id) AND (reporting_level = recb.reporting_level);
				IF(v_reporting_count is null)THEN v_reporting_count := 0; END IF;

				IF((recb.use_reporting = true) AND (recb.approve_no_reporting = true) AND (v_reporting_count = 0))THEN
					UPDATE approvals SET approve_status = 'Approved'
					WHERE (table_id = wfid) AND (approval_level = 1);

					SELECT min(approval_level) INTO v_min_level
					FROM approvals
					WHERE (table_id = wfid) AND (approve_status = 'Draft');

					UPDATE approvals SET approve_status = 'Completed'
					WHERE (table_id = wfid) AND (approval_level = v_min_level);
				END IF;
			END IF;
		END LOOP;
	END IF;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ins_approvals() RETURNS trigger AS $$
DECLARE
	reca	RECORD;
BEGIN

	IF (NEW.forward_id is not null) THEN
		SELECT workflow_phase_id, org_entity_id, app_entity_id, approval_level, table_name, table_id INTO reca
		FROM approvals
		WHERE (approval_id = NEW.forward_id);

		NEW.workflow_phase_id := reca.workflow_phase_id;
		NEW.approval_level := reca.approval_level;
		NEW.table_name := reca.table_name;
		NEW.table_id := reca.table_id;
		NEW.approve_status := 'Completed';
	END IF;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ins_approvals BEFORE INSERT ON approvals
    FOR EACH ROW EXECUTE PROCEDURE ins_approvals();


CREATE OR REPLACE FUNCTION upd_approvals() RETURNS trigger AS $$
DECLARE
	reca								RECORD;
	wfid								integer;
	v_org_id							integer;
	v_notice							boolean;
	v_advice							boolean;
	v_reporting_level					integer;
	v_use_reporting						boolean;
	v_approve_no_reporting				boolean;
	v_reporting_count					integer;
	v_sys_email_id						integer;
BEGIN

	SELECT notice, advice, org_id, reporting_level, use_reporting, approve_no_reporting
		INTO v_notice, v_advice, v_org_id, v_reporting_level, v_use_reporting, v_approve_no_reporting
	FROM workflow_phases
	WHERE (workflow_phase_id = NEW.workflow_phase_id);

	IF((NEW.approve_status = 'Completed') AND (v_use_reporting = true) AND (v_approve_no_reporting = false))THEN
		SELECT count(reporting_id) INTO v_reporting_count
		FROM reporting
		WHERE (entity_id = NEW.org_entity_id) AND (reporting_level = v_reporting_level);
		IF(v_reporting_count is null)THEN v_reporting_count := 0; END IF;

		IF(v_reporting_count = 0)THEN			---- raise an escalation email
			SELECT sys_email_id INTO v_sys_email_id
			FROM sys_emails WHERE (use_type = 65) AND (org_id = v_org_id);
			IF(v_sys_email_id is not null)THEN
				INSERT INTO sys_emailed (sys_email_id, table_id, table_name, email_type, org_id)
				VALUES (v_sys_email_id, NEW.approval_id, 'approvals', 65, v_org_id);
			END IF;
		END IF;
	END IF;

	IF(TG_OP = 'INSERT')THEN
		IF (NEW.approve_status = 'Completed') THEN
			INSERT INTO sys_emailed (table_id, table_name, email_type, org_id)
			VALUES (NEW.approval_id, TG_TABLE_NAME, 1, v_org_id);
		END IF;
		IF (NEW.approve_status = 'Approved') AND (v_advice = true) AND (NEW.forward_id is null) THEN
			INSERT INTO sys_emailed (table_id, table_name, email_type, org_id)
			VALUES (NEW.approval_id, TG_TABLE_NAME, 1, v_org_id);
		END IF;
		IF (NEW.approve_status = 'Approved') AND (v_notice = true) AND (NEW.forward_id is null) THEN
			INSERT INTO sys_emailed (table_id, table_name, email_type, org_id)
			VALUES (NEW.approval_id, TG_TABLE_NAME, 2, v_org_id);
		END IF;
	ELSE
		IF (OLD.approve_status = 'Draft') AND (NEW.approve_status = 'Completed') THEN
			INSERT INTO sys_emailed (table_id, table_name, email_type, org_id)
			VALUES (NEW.approval_id, TG_TABLE_NAME, 1, v_org_id);
		END IF;
		IF (OLD.approve_status != 'Approved') AND (NEW.approve_status = 'Approved') AND (v_advice = true) AND (NEW.forward_id is null) THEN
			INSERT INTO sys_emailed (table_id, table_name, email_type, org_id)
			VALUES (NEW.approval_id, TG_TABLE_NAME, 1, v_org_id);
		END IF;
		IF (OLD.approve_status != 'Approved') AND (NEW.approve_status = 'Approved') AND (v_notice = true) AND (NEW.forward_id is null) THEN
			INSERT INTO sys_emailed (table_id, table_name, email_type, org_id)
			VALUES (NEW.approval_id, TG_TABLE_NAME, 2, v_org_id);
		END IF;
	END IF;

	IF(TG_OP = 'INSERT') AND (NEW.forward_id is null) THEN
		INSERT INTO approval_checklists (approval_id, checklist_id, requirement, manditory, org_id)
		SELECT NEW.approval_id, checklist_id, requirement, manditory, org_id
		FROM checklists
		WHERE (workflow_phase_id = NEW.workflow_phase_id)
		ORDER BY checklist_number;
	END IF;

	RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER upd_approvals AFTER INSERT OR UPDATE ON approvals
    FOR EACH ROW EXECUTE PROCEDURE upd_approvals();

CREATE OR REPLACE FUNCTION upd_approvals(varchar(12), varchar(12), varchar(12), varchar(12)) RETURNS varchar(120) AS $$
DECLARE
	app_id						integer;
	min_level					integer;
	v_reporting_count			integer;
	v_min_level					integer;
	reca 						RECORD;
	recb						RECORD;
	recc						RECORD;
	recd						RECORD;
	mysql						varchar(240);
	msg 						varchar(120);
BEGIN
	app_id := CAST($1 as int);
	SELECT approvals.org_id, approvals.approval_id, approvals.org_id, approvals.table_name, approvals.table_id,
		approvals.approval_level, approvals.review_advice, approvals.org_entity_id,
		workflow_phases.workflow_phase_id, workflow_phases.workflow_id, workflow_phases.return_level
	INTO reca
	FROM approvals INNER JOIN workflow_phases ON approvals.workflow_phase_id = workflow_phases.workflow_phase_id
	WHERE (approvals.approval_id = app_id);

	SELECT count(approval_checklist_id) as cl_count INTO recc
	FROM approval_checklists
	WHERE (approval_id = app_id) AND (manditory = true) AND (done = false);

	IF ($3 = '1') THEN
		UPDATE approvals SET approve_status = 'Completed', completion_date = now()
		WHERE approval_id = app_id;
		msg := 'Completed';
	ELSIF ($3 = '2') AND (recc.cl_count <> 0) THEN
		msg := 'There are manditory checklist that must be checked first.';
	ELSIF ($3 = '2') AND (recc.cl_count = 0) THEN
		UPDATE approvals SET approve_status = 'Approved', action_date = now(), app_entity_id = CAST($2 as int)
		WHERE approval_id = app_id;

		SELECT min(approvals.approval_level) INTO min_level
		FROM approvals INNER JOIN workflow_phases ON approvals.workflow_phase_id = workflow_phases.workflow_phase_id
		WHERE (approvals.table_id = reca.table_id) AND (approvals.approve_status = 'Draft')
			AND (workflow_phases.advice = false) AND (workflow_phases.notice = false);

		IF(min_level is null)THEN
			mysql := 'UPDATE ' || reca.table_name || ' SET approve_status = ' || quote_literal('Approved')
			|| ', action_date = now()'
			|| ' WHERE workflow_table_id = ' || reca.table_id;
			EXECUTE mysql;

			UPDATE approval_lists SET action_date = current_timestamp, approve_status = 'Approved'
			WHERE table_id = reca.table_id;

			INSERT INTO sys_emailed (org_id, table_id, table_name, email_type)
			VALUES (reca.org_id, reca.table_id, 'vw_workflow_approvals', 1);

			FOR recb IN SELECT workflow_phase_id, advice, notice
			FROM workflow_phases
			WHERE (workflow_id = reca.workflow_id) AND (approval_level >= reca.approval_level) LOOP
				IF (recb.advice = true) or (recb.notice = true) THEN
					UPDATE approvals SET approve_status = 'Approved', action_date = now(), completion_date = now()
					WHERE (workflow_phase_id = recb.workflow_phase_id) AND (table_id = reca.table_id);
				END IF;
			END LOOP;
		ELSE
			FOR recb IN SELECT workflow_phase_id, advice, notice, reporting_level, use_reporting, approve_no_reporting
			FROM workflow_phases
			WHERE (workflow_id = reca.workflow_id) AND (approval_level <= min_level) LOOP
				SELECT count(reporting_id) INTO v_reporting_count
				FROM reporting
				WHERE (entity_id = reca.org_entity_id) AND (reporting_level = recb.reporting_level);
				IF(v_reporting_count is null)THEN v_reporting_count := 0; END IF;

				IF (recb.advice = true) or (recb.notice = true) THEN
					UPDATE approvals SET approve_status = 'Approved', action_date = now(), completion_date = now()
					WHERE (workflow_phase_id = recb.workflow_phase_id)
						AND (approve_status = 'Draft') AND (table_id = reca.table_id);

					SELECT min(approval_level) INTO v_min_level
					FROM approvals
					WHERE (table_id = reca.table_id) AND (approve_status = 'Draft');

					UPDATE approvals SET approve_status = 'Completed'
					WHERE (table_id = wfid) AND (approval_level = v_min_level);
				ELSIF((recb.use_reporting = true) AND (recb.approve_no_reporting = true) AND (v_reporting_count = 0))THEN
					UPDATE approvals SET approve_status = 'Approved', action_date = now(), completion_date = now()
					WHERE (workflow_phase_id = recb.workflow_phase_id)
						AND (approve_status = 'Draft') AND (table_id = reca.table_id);

					SELECT min(approval_level) INTO v_min_level
					FROM approvals
					WHERE (table_id = reca.table_id) AND (approve_status = 'Draft');

					UPDATE approvals SET approve_status = 'Completed'
					WHERE (table_id = wfid) AND (approval_level = v_min_level);
				ELSE
					UPDATE approvals SET approve_status = 'Completed', completion_date = now()
					WHERE (workflow_phase_id = recb.workflow_phase_id)
						AND (approve_status = 'Draft') AND (table_id = reca.table_id);
				END IF;
			END LOOP;
		END IF;
		msg := 'Approved';
	ELSIF ($3 = '3') THEN
		UPDATE approvals SET approve_status = 'Rejected',  action_date = now(), app_entity_id = CAST($2 as int)
		WHERE approval_id = app_id;

		mysql := 'UPDATE ' || reca.table_name || ' SET approve_status = ' || quote_literal('Rejected')
		|| ', action_date = now()'
		|| ' WHERE workflow_table_id = ' || reca.table_id;
		EXECUTE mysql;

		UPDATE approval_lists SET action_date = current_timestamp, approve_status = 'Rejected' WHERE table_id = reca.table_id;

		INSERT INTO sys_emailed (table_id, table_name, email_type, org_id)
		VALUES (reca.table_id, 'vw_workflow_approvals', 2, reca.org_id);
		msg := 'Rejected';
	ELSIF ($3 = '4') AND (reca.return_level = 0) THEN
		UPDATE approvals SET approve_status = 'Review',  action_date = now(), app_entity_id = CAST($2 as int)
		WHERE approval_id = app_id;

		mysql := 'UPDATE ' || reca.table_name || ' SET approve_status = ' || quote_literal('Draft')
		|| ', action_date = now()'
		|| ' WHERE workflow_table_id = ' || reca.table_id;
		EXECUTE mysql;

		UPDATE approval_lists SET action_date = current_timestamp, approve_status = 'Review' WHERE table_id = reca.table_id;

		msg := 'Forwarded to owner for review';
	ELSIF ($3 = '4') AND (reca.return_level <> 0) THEN
		UPDATE approvals SET approve_status = 'Review',  action_date = now(), app_entity_id = CAST($2 as int)
		WHERE approval_id = app_id;

		INSERT INTO approvals (org_id, workflow_phase_id, table_name, table_id, org_entity_id, escalation_days, escalation_hours, approval_level, approval_narrative, to_be_done, approve_status)
		SELECT org_id, workflow_phase_id, reca.table_name, reca.table_id, CAST($2 as int), escalation_days, escalation_hours, approval_level, phase_narrative, reca.review_advice, 'Completed'
		FROM vw_workflow_entitys
		WHERE (workflow_id = reca.workflow_id) AND (approval_level = reca.return_level)
			AND (entity_id = reca.org_entity_id)
		ORDER BY workflow_phase_id;

		UPDATE approvals SET approve_status = 'Draft' WHERE approval_id = app_id;

		msg := 'Forwarded for review';
	END IF;

	RETURN msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION upd_checklist(varchar(12), varchar(12), varchar(12)) RETURNS varchar(120) AS $$
DECLARE
	cl_id		Integer;
	reca 		RECORD;
	recc 		RECORD;
	msg 		varchar(120);
BEGIN
	cl_id := CAST($1 as int);

	SELECT approval_checklist_id, approval_id, checklist_id, requirement, manditory, done INTO reca
	FROM approval_checklists
	WHERE (approval_checklist_id = cl_id);

	IF ($3 = '1') THEN
		UPDATE approval_checklists SET done = true WHERE (approval_checklist_id = cl_id);

		SELECT count(approval_checklist_id) as cl_count INTO recc
		FROM approval_checklists
		WHERE (approval_id = reca.approval_id) AND (manditory = true) AND (done = false);
		msg := 'Checklist done.';
	ELSIF ($3 = '2') THEN
		UPDATE approval_checklists SET done = false WHERE (approval_checklist_id = cl_id);
		msg := 'Checklist not done.';
	END IF;

	RETURN msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_phase_status(boolean, boolean) RETURNS varchar(16) AS $$
DECLARE
	ps		varchar(16);
BEGIN
	ps := 'Draft';
	IF ($1 = true) THEN
		ps := 'Approved';
	END IF;
	IF ($2 = true) THEN
		ps := 'Rejected';
	END IF;

	RETURN ps;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION get_phase_email(integer) RETURNS varchar(320) AS $$
DECLARE
    myrec	RECORD;
	myemail	varchar(320);
BEGIN
	myemail := null;
	FOR myrec IN SELECT entitys.primary_email
		FROM entitys INNER JOIN entity_subscriptions ON entitys.entity_id = entity_subscriptions.entity_id
		WHERE (entity_subscriptions.entity_type_id = $1) LOOP

		IF (myemail is null) THEN
			IF (myrec.primary_email is not null) THEN
				myemail := myrec.primary_email;
			END IF;
		ELSE
			IF (myrec.primary_email is not null) THEN
				myemail := myemail || ', ' || myrec.primary_email;
			END IF;
		END IF;

	END LOOP;

	RETURN myemail;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION get_phase_entitys(integer) RETURNS varchar(320) AS $$
DECLARE
    myrec			RECORD;
	myentitys		varchar(320);
BEGIN
	myentitys := null;
	FOR myrec IN SELECT entitys.entity_name
		FROM entitys INNER JOIN entity_subscriptions ON entitys.entity_id = entity_subscriptions.entity_id
		WHERE (entity_subscriptions.entity_type_id = $1) LOOP

		IF (myentitys is null) THEN
			IF (myrec.entity_name is not null) THEN
				myentitys := myrec.entity_name;
			END IF;
		ELSE
			IF (myrec.entity_name is not null) THEN
				myentitys := myentitys || ', ' || myrec.entity_name;
			END IF;
		END IF;

	END LOOP;

	RETURN myentitys;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_workflow_narrative(varchar(64), integer) RETURNS text AS $$
DECLARE
	msg 		text;
BEGIN

	msg := '';


	RETURN msg;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_default_country(int) RETURNS char(2) AS $$
	SELECT default_country_id::varchar(2)
	FROM orgs
	WHERE (org_id = $1);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_default_currency(int) RETURNS int AS $$
	SELECT currency_id
	FROM orgs
	WHERE (org_id = $1);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_start_month(varchar(12)) RETURNS varchar(12) AS $$
	SELECT '01/' || to_char(current_date, 'MM/YYYY');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_end_month(varchar(12)) RETURNS varchar(12) AS $$
	SELECT to_char((to_char(current_date, 'YYYY-MM') || '-01')::date + '1 month'::interval - '1 day'::interval, 'DD/MM/YYYY');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_start_year(varchar(12)) RETURNS varchar(12) AS $$
	SELECT '01/01/' || to_char(current_date, 'YYYY');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_end_year(varchar(12)) RETURNS varchar(12) AS $$
	SELECT '31/12/' || to_char(current_date, 'YYYY');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_current_year(varchar(12)) RETURNS varchar(12) AS $$
	SELECT to_char(current_date, 'YYYY');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_config_value(varchar(254)) RETURNS text AS $$
	SELECT config_value FROM sys_configs
	WHERE (config_name = $1);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_config_value(int) RETURNS text AS $$
	SELECT config_value FROM sys_configs
	WHERE (sys_config_id = $1);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_config_value(int, int) RETURNS text AS $$
	SELECT config_value FROM sys_configs
	WHERE (org_id = $1) AND (config_type_id = $2);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_currency_rate(integer, integer) RETURNS real AS $$
	SELECT max(exchange_rate)
	FROM currency_rates
	WHERE (org_id = $1) AND (currency_id = $2)
		AND (exchange_date = (SELECT max(exchange_date) FROM currency_rates WHERE (org_id = $1) AND (currency_id = $2)));
$$ LANGUAGE SQL;

CREATE FUNCTION get_reporting_list(integer) RETURNS varchar(320) AS $$
DECLARE
    myrec	RECORD;
	mylist	varchar(320);
BEGIN
	mylist := null;
	FOR myrec IN SELECT entitys.entity_name
		FROM reporting INNER JOIN entitys ON reporting.report_to_id = entitys.entity_id
		WHERE (reporting.primary_report = true) AND (reporting.entity_id = $1)
	LOOP

		IF (mylist is null) THEN
			mylist := myrec.entity_name;
		ELSE
			mylist := mylist || ', ' || myrec.entity_name;
		END IF;
	END LOOP;

	RETURN mylist;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION excel_time_to_24hrs(varchar(120), varchar(50))RETURNS time without time zone AS $$
	SELECT  (TO_CHAR(((SELECT (((ROUND(($1::numeric),8) * 24* 60 * 60 )* 1000)+1) seconds) 
	|| ' milliseconds')::interval, $2))::time without time zone
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_excel_date(excel_date integer) RETURNS date AS $$
   SELECT '1899-12-31'::date + excel_date;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION get_excel_timestamp(varchar(50)) RETURNS timestamp AS $$
DECLARE
	ans					timestamp;
	v_date_time			real;
	v_date				integer;
	v_time				integer;
BEGIN

	v_date_time := $1::real;
	v_date := trunc(v_date_time);
	v_time := 24 * 60 * 60 * 1000 * (v_date_time - v_date);

	ans := '1899-12-31'::date + v_date;
	ans := ans + (v_time::varchar || ' milliseconds')::interval;

	RETURN ans;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION amount_in_words(n BIGINT) RETURNS TEXT AS $$
DECLARE
	e 		TEXT;
BEGIN

	WITH 
		Below20(Word, Id) AS (
			VALUES ('Zero', 0), ('One', 1),( 'Two', 2 ), ( 'Three', 3), ( 'Four', 4 ), ( 'Five', 5 ), ( 'Six', 6 ), ( 'Seven', 7 ),
			( 'Eight', 8), ( 'Nine', 9), ( 'Ten', 10), ( 'Eleven', 11 ),( 'Twelve', 12 ), ( 'Thirteen', 13 ), ( 'Fourteen', 14),
			( 'Fifteen', 15 ), ('Sixteen', 16 ), ( 'Seventeen', 17),
			('Eighteen', 18 ), ( 'Nineteen', 19 )
		),
  		Below100(Word, Id) AS (
			VALUES ('Twenty', 2), ('Thirty', 3),('Forty', 4), ('Fifty', 5),
			('Sixty', 6), ('Seventy', 7), ('Eighty', 8), ('Ninety', 9)
		)
		
		SELECT CASE
			WHEN n = 0 THEN  ''
			WHEN n BETWEEN 1 AND 19
			THEN (SELECT Word FROM Below20 WHERE ID=n)
			WHEN n BETWEEN 20 AND 99
				THEN  (SELECT Word FROM Below100 WHERE ID=n/10) ||  '-'  ||
				amount_in_words( n % 10)
			WHEN n BETWEEN 100 AND 999
				THEN  (amount_in_words( n / 100)) || ' Hundred ' ||
				amount_in_words( n % 100)
			WHEN n BETWEEN 1000 AND 999999
				THEN  (amount_in_words( n / 1000)) || ' Thousand ' ||
				amount_in_words( n % 1000)
			WHEN n BETWEEN 1000000 AND 999999999
				THEN  (amount_in_words( n / 1000000)) || ' Million ' ||
				amount_in_words( n % 1000000)
			WHEN n BETWEEN 1000000000 AND 999999999999
				THEN  (amount_in_words( n / 1000000000)) || ' Billion ' ||
				amount_in_words( n % 1000000000)
			WHEN n BETWEEN 1000000000000 AND 999999999999999
				THEN  (amount_in_words( n / 1000000000000)) || ' Trillion ' ||
				amount_in_words( n % 1000000000000)
			WHEN n BETWEEN 1000000000000000 AND 999999999999999999
				THEN  (amount_in_words( n / 1000000000000000)) || ' Quadrillion ' ||
				amount_in_words( n % 1000000000000000)
			WHEN n BETWEEN 1000000000000000000 AND 999999999999999999999
				THEN  (amount_in_words( n / 1000000000000000000)) || ' Quintillion ' ||
				amount_in_words( n % 1000000000000000000)
			ELSE ' INVALID INPUT' END INTO e;
		
		e := RTRIM(e);
		IF RIGHT(e,1)='-' THEN
			e := RTRIM(LEFT(e,length(e)-1));
		END IF;
			
	RETURN e;
END;
$$ LANGUAGE plpgsql;

--- Data
INSERT INTO currency (currency_id, currency_name, currency_symbol) VALUES
(1, 'Kenya Shillings', 'KES'),
(2, 'US Dollar', 'USD'),
(3, 'British Pound', 'BPD'),
(4, 'Euro', 'ERO');

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

INSERT INTO orgs (org_id, org_name, org_sufix, currency_id, logo, letter_head) VALUES
(0, 'default', 'dc', 1, 'logo.png', 'letter_head.jpg');

UPDATE currency SET org_id = 0;
SELECT pg_catalog.setval('currency_currency_id_seq', 4, true);

INSERT INTO currency_rates (currency_rate_id, org_id, currency_id, exchange_rate) VALUES
(0, 0, 1, 1);

INSERT INTO sys_languages (sys_language_id, sys_language_name) VALUES
(0, 'English'),
(1, 'French'),
(2, 'Arabic'),
(3, 'Simple Chinese'),
(4, 'Traditional Chinese'),
(5, 'Spanish');

INSERT INTO use_keys (use_key_id, use_key_name, use_function) VALUES
(0, 'System Admins', 0),
(1, 'Staff', 0),
(2, 'Client', 0),
(3, 'Supplier', 0),
(4, 'Applicant', 0),
(5, 'Subscription', 0),
(6, 'User', 0);

INSERT INTO entity_types (org_id, entity_type_id, entity_type_name, entity_role, use_key_id, start_view) VALUES
(0, 0, 'System Admins', 'sysadmin', 0, null),
(0, 1, 'Staff', 'staff', 1, null),
(0, 2, 'Client', 'client', 2, null),
(0, 3, 'Supplier', 'supplier', 3, null),
(0, 4, 'Applicant', 'applicant', 4, '10:0'),
(0, 5, 'Subscription', 'subscription', 5, null),
(0, 6, 'User', 'user', 6, null);
SELECT pg_catalog.setval('entity_types_entity_type_id_seq', 6, true);

INSERT INTO entitys (entity_id, org_id, entity_type_id, use_key_id, sys_language_id, user_name, entity_name, primary_email, entity_leader, super_user, no_org, first_password) VALUES
(0, 0, 0, 0, 0, 'root', 'root', 'root@localhost', true, true, false, 'baraza'),
(1, 0, 0, 0, 0, 'repository', 'repository', 'repository@localhost', true, false, false, 'baraza');
SELECT pg_catalog.setval('entitys_entity_id_seq', 1, true);
