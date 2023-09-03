DROP TABLE huduma_data;
CREATE TABLE huduma_data (
	loan_id 		REAL NOT NULL PRIMARY KEY,
	s_no			REAL NOT NULL,
	member_name		VARCHAR (150) NOT NULL,
	member_number		VARCHAR (50) NOT NULL,
	loan_amount		DECIMAL(15,2),
	loan_period_months 	REAL NOT NULL,
	disbursement_date 	VARCHAR (15),
	repayment_duration 	VARCHAR (50),
	monthly_repayment 	REAL,
	loan_interest 		REAL,

	months_paid_amounts 	real[],
	months_paid_interests 	real[]
);
select * from huduma_data;

-- loan_id, s_no, member_name, member_number, loan_amount,loan_period_months, disbursement_date, repayment_duration, monthly_repayment, loan_interest, months_paid_amounts, months_paid_interests