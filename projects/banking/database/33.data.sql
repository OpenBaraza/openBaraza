
--- Create use key types
INSERT INTO use_keys (use_key_id, use_key_name, use_function) VALUES 
(100, 'Customers', 0),
(101, 'Receipts', 4),
(102, 'Payments', 4),
(103, 'Opening Account', 4),
(104, 'Transfer', 4),
(105, 'Loan Intrests', 4),
(106, 'Loan Penalty', 4),
(107, 'Loan Payment', 4),
(108, 'Loan Disbursement', 4),
(109, 'Account Intrests', 4),
(110, 'Account Penalty', 4),
(201, 'Initial Charges', 4),
(202, 'Transaction Charges', 4);

INSERT INTO activity_frequency (activity_frequency_id, activity_frequency_name) 
VALUES (1, 'Once'), (4, 'Monthly');
--- (1, 'Once'), (2, 'Daily'), (3, 'Weekly'), (4, 'Monthly'), (5, 'Quartely'), (6, 'Half Yearly'), (7, 'Yearly');

INSERT INTO activity_status (activity_status_id, activity_status_name) VALUES 
(1, 'Completed'),
(2, 'UnCleared'),
(3, 'Processing'),
(4, 'Commited');

INSERT INTO entity_types (org_id, use_key_id, entity_type_name, entity_role) VALUES (0, 100, 'Bank Customers', 'client');
INSERT INTO locations (org_id, location_name) VALUES (0, 'Head Office');
INSERT INTO departments (org_id, department_name) VALUES (0, 'Board of Directors');

INSERT INTO collateral_types (org_id, collateral_type_name) VALUES 
(0, 'Land Title'),
(0, 'Car Log book');

INSERT INTO activity_types (activity_type_id, cr_account_id, dr_account_id, use_key_id, org_id, activity_type_name, is_active) VALUES 
(1, 34005, 34005, 202, 0, 'No Charges', true),
(2, 34005, 34005, 101, 0, 'Cash Deposits', true),
(3, 34005, 34005, 101, 0, 'Cheque Deposits', true),
(4, 34005, 34005, 101, 0, 'MPESA Deposits', true),
(5, 34005, 34005, 102, 0, 'Cash Withdrawal', true),
(6, 34005, 34005, 102, 0, 'Cheque Withdrawal', true),
(7, 34005, 34005, 102, 0, 'MPESA Withdrawal', true),
(8, 70015, 34005, 105, 0, 'Loan Intrests', true),
(9, 70025, 34005, 106, 0, 'Loan Penalty', true),
(10, 34005, 34005, 107, 0, 'Loan Payment', true),
(11, 34005, 34005, 108, 0, 'Loan Disbursement', true),
(12, 34005, 34005, 104, 0, 'Account Transfer', true),
(14, 70015, 34005, 109, 0, 'Account Intrests', true),
(15, 70025, 34005, 110, 0, 'Account Penalty', true),
(21, 70020, 34005, 201, 0, 'Account opening charges', true),
(22, 70020, 34005, 202, 0, 'Transfer fees', true);
SELECT pg_catalog.setval('activity_types_activity_type_id_seq', 22, true);
UPDATE activity_types SET activity_type_no = activity_type_id;

INSERT INTO interest_methods (interest_method_id, activity_type_id, org_id, interest_method_name, formural, account_number, reducing_balance, reducing_payments) VALUES 
(0, 8, 0, 'No Intrest', null, '400000003', false, false),
(1, 8, 0, 'Loan reducing balance', 'get_intrest(1, loan_id, period_id)', '400000003', true, false),
(2, 8, 0, 'Loan Fixed Intrest', 'get_intrest(2, loan_id, period_id)', '400000003', false, false),
(3, 14, 0, 'Savings intrest', 'get_intrest(3, deposit_account_id, period_id)', '400000003', false, false),
(4, 8, 0, 'Loan reducing balance and payments', 'get_intrest(1, loan_id, period_id)', '400000003', true, true);
SELECT pg_catalog.setval('interest_methods_interest_method_id_seq', 4, true);
UPDATE interest_methods SET interest_method_no = interest_method_id;

INSERT INTO penalty_methods (penalty_method_id, activity_type_id, org_id, penalty_method_name, formural, account_number) VALUES 
(0, 9, 0, 'No penalty', null, '400000004'),
(1, 9, 0, 'Loan Penalty 15', 'get_penalty(1, loan_id, period_id, 15)', '400000004'),
(2, 15, 0, 'Account Penalty 15', 'get_penalty(1, deposit_account_id, period_id, 15)', '400000004');
SELECT pg_catalog.setval('penalty_methods_penalty_method_id_seq', 2, true);
UPDATE penalty_methods SET penalty_method_no = penalty_method_id;

INSERT INTO products (product_id, activity_frequency_id, interest_method_id, penalty_method_id, currency_id, org_id, product_name, description, loan_account, is_active, interest_rate, min_opening_balance, lockin_period_frequency, minimum_balance, maximum_balance, minimum_day, maximum_day, minimum_trx, maximum_trx, less_initial_fee, approve_status) VALUES
(0, 4, 0, 0, 1, 0, 'Banking', 'Banking', false, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 'Approved'),
(1, 4, 0, 0, 1, 0, 'Transaction account', 'Account to handle transactions', false, true, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 'Approved'),
(2, 4, 1, 1, 1, 0, 'Basic loans', 'Basic loans', true, true, 12, 0, 0, 0, 0, 0, 0, 0, 0, false, 'Approved'),
(3, 4, 3, 0, 1, 0, 'Savings account', 'Account to handle savings', false, true, 3, 0, 0, 0, 0, 0, 0, 0, 0, false, 'Approved'),
(4, 4, 2, 1, 1, 0, 'Compound loans', 'Compound loans', true, true, 12, 0, 0, 0, 0, 0, 0, 0, 0, true, 'Approved'),
(5, 4, 4, 1, 1, 0, 'Reducing balance loans', 'Reducing balance loans', true, true, 12, 0, 0, 0, 0, 0, 0, 0, 0, true, 'Approved');
SELECT pg_catalog.setval('products_product_id_seq', 5, true);
UPDATE products SET product_no = product_id;

INSERT INTO account_definations (activity_type_id, charge_activity_id, activity_frequency_id, product_id, org_id, account_defination_name, start_date, end_date, account_number, is_active) VALUES 
(2, 1, 1, 0, 0, 'Cash Deposit', '2017-01-01', NULL, '400000001', true),
(3, 1, 1, 0, 0, 'Cheque Deposit', '2017-01-01', NULL, '400000001', true),
(4, 1, 1, 0, 0, 'MPESA Deposit', '2017-01-01', NULL, '400000001', true),
(5, 1, 1, 0, 0, 'Cash Withdraw', '2017-01-01', NULL, '400000001', true),
(6, 1, 1, 0, 0, 'Cheque Withdraw', '2017-01-01', NULL, '400000001', true),
(7, 1, 1, 0, 0, 'MPESA Withdraw', '2017-01-01', NULL, '400000001', true);
INSERT INTO account_definations (activity_type_id, charge_activity_id, activity_frequency_id, product_id, org_id, account_defination_name, start_date, end_date, account_number, is_active) VALUES 
(2, 1, 1, 1, 0, 'Cash Deposit', '2017-01-01', NULL, '400000001', true),
(3, 1, 1, 1, 0, 'Cheque Deposit', '2017-01-01', NULL, '400000001', true),
(4, 1, 1, 1, 0, 'MPESA Deposit', '2017-01-01', NULL, '400000001', true),
(5, 1, 1, 1, 0, 'Cash Withdraw', '2017-01-01', NULL, '400000001', true),
(6, 1, 1, 1, 0, 'Cheque Withdraw', '2017-01-01', NULL, '400000001', true),
(7, 1, 1, 1, 0, 'MPESA Withdraw', '2017-01-01', NULL, '400000001', true);
INSERT INTO account_definations (activity_type_id, charge_activity_id, activity_frequency_id, product_id, org_id, account_defination_name, start_date, end_date, fee_ps, account_number, is_active, has_charge) 
VALUES (12, 22, 1, 1, 0, 'Transfer', '2017-01-01', NULL, 1, '400000002', true, true);
INSERT INTO account_definations (activity_type_id, charge_activity_id, activity_frequency_id, product_id, org_id, account_defination_name, start_date, end_date, fee_amount, account_number, is_active, has_charge) 
VALUES (21, 1, 1, 1, 0, 'Opening account', '2017-01-01', NULL, 1000, '400000002', true, true);

INSERT INTO account_definations (activity_type_id, charge_activity_id, activity_frequency_id, product_id, org_id, account_defination_name, start_date, end_date, account_number, is_active, has_charge, fee_amount) VALUES
(21, 1, 1, 2, 0, 'Opening account', '2017-01-01', NULL, '400000002', true, true, 2000),
(11, 1, 1, 2, 0, 'Loan Disbursement', '2017-01-01', NULL, '400000001', true, false, 0),
(10, 1, 1, 2, 0, 'Loan Payment', '2017-01-01', NULL, '400000001', true, false, 0);

INSERT INTO account_definations (activity_type_id, charge_activity_id, activity_frequency_id, product_id, org_id, account_defination_name, start_date, end_date, account_number, is_active, has_charge, fee_ps) VALUES 
(2, 1, 1, 3, 0, 'Cash Deposit', '2017-01-01', NULL, '400000001', true, false, 0),
(3, 1, 1, 3, 0, 'Cheque Deposit', '2017-01-01', NULL, '400000001', true, false, 0),
(4, 1, 1, 3, 0, 'MPESA Deposit', '2017-01-01', NULL, '400000001', true, false, 0),
(5, 1, 1, 3, 0, 'Cash Withdraw', '2017-01-01', NULL, '400000001', true, false, 0),
(6, 1, 1, 3, 0, 'Cheque Withdraw', '2017-01-01', NULL, '400000001', true, false, 0),
(7, 1, 1, 3, 0, 'MPESA Withdraw', '2017-01-01', NULL, '400000001', true, false, 0),
(12, 22, 1, 3, 0, 'Transfer', '2017-01-01', NULL, '400000002', true, true, 1);

INSERT INTO account_definations (activity_type_id, charge_activity_id, activity_frequency_id, product_id, org_id, account_defination_name, start_date, end_date, account_number, is_active, has_charge, fee_amount) VALUES 
(21, 1, 1, 4, 0, 'Opening account', '2017-01-01', NULL, '400000002', true, true, 1500),
(11, 1, 1, 4, 0, 'Loan Disbursement', '2017-01-01', NULL, '400000001', true, false, 0),
(10, 1, 1, 4, 0, 'Loan Payment', '2017-01-01', NULL, '400000001', true, false, 0);

INSERT INTO account_definations (activity_type_id, charge_activity_id, activity_frequency_id, product_id, org_id, account_defination_name, start_date, end_date, account_number, is_active, has_charge, fee_amount) VALUES 
(21, 1, 1, 5, 0, 'Opening account', '2017-01-01', NULL, '400000002', true, true, 1500),
(11, 1, 1, 5, 0, 'Loan Disbursement', '2017-01-01', NULL, '400000001', true, false, 0),
(10, 1, 1, 5, 0, 'Loan Payment', '2017-01-01', NULL, '400000001', true, false, 0);


--- Create Initial customer and customer account
INSERT INTO customers (customer_id, org_id, business_account, customer_name, identification_number, identification_type, client_email, telephone_number, date_of_birth, nationality, approve_status)
VALUES (0, 0, 2, 'OpenBaraza Bank', '0', 'Org', 'info@openbaraza.org', '+254', current_date, 'KE', 'Approved');

INSERT INTO deposit_accounts (customer_id, product_id, org_id, is_active, approve_status, narrative, minimum_balance) VALUES 
(0, 0, 0, true, 'Approved', 'Deposits', -100000000000),
(0, 0, 0, true, 'Approved', 'Charges', -100000000000),
(0, 0, 0, true, 'Approved', 'Interest', -100000000000),
(0, 0, 0, true, 'Approved', 'Penalty', -100000000000),
(0, 0, 0, true, 'Approved', 'Loan', -100000000000);


---- Workflow setup
INSERT INTO workflows (workflow_id, org_id, source_entity_id, workflow_name, table_name, table_link_field, table_link_id, approve_email, reject_email, approve_file, reject_file, details) VALUES
(20, 0, 0, 'Customer Application', 'customers', NULL, NULL, 'Request approved', 'Request rejected', NULL, NULL, NULL),
(21, 0, 0, 'Account opening', 'deposit_accounts', NULL, NULL, 'Request approved', 'Request rejected', NULL, NULL, NULL),
(22, 0, 0, 'Loan Application', 'loans', NULL, NULL, 'Request approved', 'Request rejected', NULL, NULL, NULL),
(23, 0, 0, 'Guarantees Application', 'guarantees', NULL, NULL, 'Request approved', 'Request rejected', NULL, NULL, NULL),
(24, 0, 0, 'Collaterals Application', 'collaterals', NULL, NULL, 'Request approved', 'Request rejected', NULL, NULL, NULL),
(25, 0, 0, 'Customer Application', 'applicants', NULL, NULL, 'Request approved', 'Request rejected', NULL, NULL, NULL),
(26, 0, 6, 'Account opening - Customer', 'deposit_accounts', NULL, NULL, 'Request approved', 'Request rejected', NULL, NULL, NULL),
(27, 0, 6, 'Loan Application - Customer', 'loans', NULL, NULL, 'Request approved', 'Request rejected', NULL, NULL, NULL);
SELECT pg_catalog.setval('workflows_workflow_id_seq', 30, true);

INSERT INTO workflow_phases (workflow_phase_id, org_id, workflow_id, approval_entity_id, approval_level, return_level, escalation_days, escalation_hours, required_approvals, advice, notice, phase_narrative, advice_email, notice_email, advice_file, notice_file, details) VALUES
(20, 0, 20, 0, 1, 0, 0, 3, 1, false, false, 'Approve', 'For your approval', 'Phase approved', NULL, NULL, NULL),
(21, 0, 21, 0, 1, 0, 0, 3, 1, false, false, 'Approve', 'For your approval', 'Phase approved', NULL, NULL, NULL),
(22, 0, 22, 0, 1, 0, 0, 3, 1, false, false, 'Approve', 'For your approval', 'Phase approved', NULL, NULL, NULL),
(23, 0, 23, 0, 1, 0, 0, 3, 1, false, false, 'Approve', 'For your approval', 'Phase approved', NULL, NULL, NULL),
(24, 0, 24, 0, 1, 0, 0, 3, 1, false, false, 'Approve', 'For your approval', 'Phase approved', NULL, NULL, NULL),
(25, 0, 25, 0, 1, 0, 0, 3, 1, false, false, 'Approve', 'For your approval', 'Phase approved', NULL, NULL, NULL),
(26, 0, 26, 0, 1, 0, 0, 3, 1, false, false, 'Approve', 'For your approval', 'Phase approved', NULL, NULL, NULL),
(27, 0, 27, 0, 1, 0, 0, 3, 1, false, false, 'Approve', 'For your approval', 'Phase approved', NULL, NULL, NULL);
SELECT pg_catalog.setval('workflow_phases_workflow_phase_id_seq', 30, true);


------ emails

INSERT INTO sys_emails (use_type, org_id, sys_email_name, title, details) VALUES 
(1, 0, 'Application', 'Thank you for your Application', 'Thank you {{name}} for your application.<br><br>
Your user name is {{username}}<br> 
Your password is {{password}}<br><br>
Regards<br>
Human Resources Manager<br>'),
(2, 0, 'New Customer', 'Your credentials ', 'Hello {{name}},<br><br>
Your credentials to the banking system have been created.<br>
Your user name is {{username}}<br>
Regards<br>
Human Resources Manager<br>'),
(3, 0, 'Password reset', 'Password reset', 'Hello {{name}},<br><br>
Your password has been reset to:<br><br>
Your user name is {{username}}<br> 
Your password is {{password}}<br><br>
Regards<br>
Human Resources Manager<br>'),
(4, 0, 'Subscription', 'Subscription', 'Hello {{name}},<br><br>
Welcome to OpenBaraza SaaS Platform<br><br>
Your password is:<br><br>
Your user name is {{username}}<br> 
Your password is {{password}}<br><br>
Regards,<br>
OpenBaraza<br>'),
(5, 0, 'Subscription', 'Subscription', 'Hello {{name}},<br><br>
Your OpenBaraza SaaS Platform application has been approved<br><br>
Welcome to OpenBaraza SaaS Platform<br><br>
Regards,<br>
OpenBaraza<br>');


