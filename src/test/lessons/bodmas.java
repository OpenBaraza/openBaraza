
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class bodmas implements ActionListener {

	JTextField txtProblem, txtSolution;

	public static void main(String args[]) {
		bodmas bs = new bodmas();
	}

	public bodmas() {
		JPanel pMain = new JPanel(new GridLayout(0,2));
		JLabel lblProblem = new JLabel("Problem : ");
		JLabel lblSolution = new JLabel("Solution : ");
		txtProblem = new JTextField(100);
		txtSolution = new JTextField(100);
		JButton btnSolve = new JButton("Solve");
		btnSolve.addActionListener(this);
		
		// Testing string
		txtProblem.setText("(15.4 +  17 * 3.2) * 4");
		txtProblem.setText("15.4 +  17 * 3.2 - 12");
		
		pMain.add(lblProblem); pMain.add(txtProblem);
		pMain.add(lblSolution); pMain.add(txtSolution); 
		pMain.add(btnSolve);

		// Create the frame and add components
		JFrame frame = new JFrame("BODMAS");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(pMain, BorderLayout.CENTER);

		frame.setSize(700, 200);
		frame.setVisible(true);
	}
	
	
	public void actionPerformed(ActionEvent ev) {
		String problem = txtProblem.getText();
		
		System.out.println(problem);
		evaluate(problem.replace(" ", "").toLowerCase());
	}
	
	public void evaluate(String problem) {
		System.out.println(problem);
		problem = problem.replace(")(", ")*(");
		
		// Reading the string and creating a list of operators and numbers
		List<Operand> opl = new ArrayList<Operand>();
		Stack<Character> bracket = new Stack<Character>();
		char[] pc = problem.toCharArray();
		String err = null;
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < pc.length; i++) {
	System.out.println(pc[i]);
			if((pc[i] == '.') || ((pc[i] >= '0') && (pc[i] <= '9'))) {
				sb.append(pc[i]);
			} else {
				if(sb.length() > 0) {	// add number to list
					opl.add(new Operand(sb.toString(), null));
					sb = new StringBuffer();
				}
				
				if((pc[i] == '^') || (pc[i] == '/') || (pc[i] == '*') || (pc[i] == '+') || (pc[i] == '-')) {
					opl.add(new Operand(null, pc[i]));
				} else if(pc[i] == '(') {
					opl.add(new Operand(null, pc[i]));
					bracket.push(pc[i]);
				} else if(pc[i] == ')') {
					opl.add(new Operand(null, pc[i]));
					bracket.pop();
				} else {
					err = "You need a number or operator () / * + -";
				}
			}
		}
		// Add last number
		if((sb.length() > 0) && (pc[pc.length - 1] != ')')) opl.add(new Operand(sb.toString(), null));
		
		if(!bracket.empty()) err = "You need to close brackets correctly";
		
		// Validate my number and operators collection
		System.out.println("-----------");
		
		// Evaluate the function
		int bracketNo = 0;
		Stack<Character> ops = new Stack<Character>();
		Stack<Float> values = new Stack<Float>();
		for(Operand op : opl) {
			if(op.isNumber()) {
				System.out.println(op.getValue());
				values.push(op.getValue());
			} else if(op.getOperand() == '(') {
				ops.push(op.getOperand());
				bracketNo++;
			} else if(op.getOperand() == ')') {
				ops.push(op.getOperand());
				Stack<Character> bOps = new Stack<Character>();
				Stack<Float> bValues = new Stack<Float>();
				while(!ops.empty() && ops.peek() != '(') {
					bOps.push(ops.pop());
					bValues.push(values.pop());
				}
				values.push(operate(bOps, bValues));
				ops.pop();
				bracketNo--;
			} else {
				Character oc = op.getOperand();
				while((!ops.empty()) && (precedence(ops.peek()) >= precedence(oc))) {
					Float num2 = values.pop();
					Float num1 = values.pop();
					values.push(operate(num1, ops.pop(), num2));
				}
				ops.push(oc);
			}
		}
		while(!ops.empty()) {
			Float num2 = values.pop();
			Float num1 = values.pop();
			values.push(operate(num1, ops.pop(), num2));
		}
		
		System.out.println("-----------");
		System.out.println(values.pop());
	}
	
	// Try and ensure you only handle * and / before + and -
	public int precedence(Character op) { 
		if(op == '+'||op == '-') return 1; 
		if(op == '*'||op == '/') return 2; 
		return 0; 
	}
	
	public Float operate(Stack<Character> ops, Stack<Float> values) {
		Float ans = new Float(0);
		Character oc = ops.pop();
		
		while((!ops.empty()) && (precedence(ops.peek()) >= precedence(oc))) {
			Float num2 = values.pop();
			Float num1 = values.pop();
			values.push(operate(num1, ops.pop(), num2));
		}
		
		ans = values.pop();

		return ans;
	}
	
	public Float operate(Float num1, Character op, Float num2) {
		Float ans = new Float(0);
		switch(op) {
		case '^':
			break;
		case '/':
			if(num2 == 0) return null;
			else ans = num1 / num2;
			break;
		case '*':
			ans = num1 * num2;
			break;
		case '+':
			ans = num1 + num2;
			break;
		case '-':
			ans = num1 - num2;
			break;
		}
		return ans;
	}
	
	class Operand {
		Float value = null;
		Character type = null;
		
		public Operand(String sValue, Character nType) {
			if(sValue == null) this.type = nType;
    		else this.value = new Float(sValue);
		}
		
		public boolean isNumber() {
			if(value == null) return false;
			return true;
		}
		
		public Float getValue() { return value; }
		public Character getOperand() { return type; }
	}
}
