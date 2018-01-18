/**
 *
 */
package eg.edu.alexu.csd.oop.view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.PropertyConfigurator;

import eg.edu.alexu.csd.oop.DBparser.SQLparser;

/**
 * @author Personal
 *
 */
public class DriverGUI {

	private JFrame frame;
	private JTextField pathText;
	private JTextField queryText;
	private JButton executeQuerybtn;
	private JButton addBatchbtn;
	private JButton executeBatchbtn;
	private JButton clearBatchbtn;
	private JButton addPath;
	private JLabel resultArea;
	private Controller control;
	private JButton closebtn;
	private JButton timeOutbtn;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					DriverGUI window = new DriverGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DriverGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		PropertyConfigurator.configure("log4j.properties");

		control = new Controller();

		frame = new JFrame();
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		frame.setBounds(100, 100, 560, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel queryLabel = new JLabel("Enter Query");
		queryLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		queryLabel.setBackground(Color.GREEN);
		queryLabel.setForeground(Color.BLACK);
		queryLabel.setHorizontalAlignment(SwingConstants.LEFT);
		queryLabel.setBounds(10, 63, 108, 34);
		frame.getContentPane().add(queryLabel);

		pathText = new JTextField();
		pathText.setForeground(Color.BLACK);
		pathText.setBackground(Color.WHITE);
		queryLabel.setLabelFor(pathText);
		pathText.setBounds(143, 19, 269, 34);
		frame.getContentPane().add(pathText);
		pathText.setColumns(10);

		JLabel dbLabel = new JLabel("Enter Database Path");
		dbLabel.setHorizontalAlignment(SwingConstants.LEFT);
		dbLabel.setForeground(Color.BLACK);
		dbLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		dbLabel.setBackground(Color.GREEN);
		dbLabel.setBounds(10, 18, 123, 34);
		frame.getContentPane().add(dbLabel);

		queryText = new JTextField();
		queryText.setForeground(Color.BLACK);
		queryText.setColumns(10);
		queryText.setBackground(Color.WHITE);
		queryText.setBounds(143, 64, 378, 34);
		frame.getContentPane().add(queryText);

		executeQuerybtn = new JButton("Execute Query");
		executeQuerybtn.setEnabled(false);
		executeQuerybtn.setBounds(10, 108, 123, 23);
		frame.getContentPane().add(executeQuerybtn);
		executeQuerybtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = control.execute(queryText.getText());
				SQLparser parser = new SQLparser();
				if (!result.equalsIgnoreCase("Successful")) {
					resultArea.setText(result);

				} else {
					if (control.getChangedRows() != 0) {
						resultArea.setText("Query executed Successfully : " + control.getChangedRows() + " row/s affected");
					} else if (parser.checkSyntax(queryText.getText())
							&& parser.getStatement().equalsIgnoreCase("SELECT")) {
						Object[][] ans = control.s.selectedRows();
						int[] temp = control.s.db.getDimension();
						int row = temp[0];
						int col = temp[1];
						System.out.println(row);
						System.out.println(col);

						StringBuffer s = new StringBuffer();
						for (int i = 0; i < row; i++) {
							for (int j = 0; j < col; j++) {
								s.append(ans[i][j] + "        ");
							}

							s.append("\n");
						}
						resultArea.setText(s.toString());
					} else {
						resultArea.setText("Query executed Successfully");
					}
				}
				queryText.setText("");
			}
		});

		addBatchbtn = new JButton("Add batch");
		addBatchbtn.setEnabled(false);
		addBatchbtn.setBounds(136, 109, 100, 23);
		frame.getContentPane().add(addBatchbtn);
		addBatchbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = control.action("ADD", queryText.getText());
				if (result.equalsIgnoreCase("Successful")) {
					resultArea.setText("Batch was added successfully");
				} else {
					resultArea.setText(result);
				}
				queryText.setText("");
			}
		});

		clearBatchbtn = new JButton("Clear batch");
		clearBatchbtn.setEnabled(false);
		clearBatchbtn.setBounds(242, 109, 100, 23);
		frame.getContentPane().add(clearBatchbtn);
		clearBatchbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = control.action("CLEAR", queryText.getText());
				if (result.equalsIgnoreCase("Successful")) {
					resultArea.setText("Batch was cleared successfully");
				} else {
					resultArea.setText(result);
				}
				queryText.setText("");
			}
		});

		executeBatchbtn = new JButton("Execute batch");
		executeBatchbtn.setEnabled(false);
		executeBatchbtn.setBounds(350, 109, 123, 23);
		frame.getContentPane().add(executeBatchbtn);
		executeBatchbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] result = control.executeBat();
				String error = control.getMsg();
				if (!error.equalsIgnoreCase("Successful")) {
					resultArea.setText(error);
					queryText.setText("");
					return;
				}
				StringBuffer s = new StringBuffer();
				for (int i = 0; i < result.length; i++) {
					s.append("Batch" + (i+1) + "was successfull : " + result[i] + " rows are affected" + "\n");
				}
				resultArea.setText(s.toString());
				queryText.setText("");

			}
		});

		addPath = new JButton("Enter");
		addPath.setFont(new Font("Tahoma", Font.BOLD, 15));
		addPath.setBounds(432, 17, 89, 34);
		frame.getContentPane().add(addPath);
		addPath.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean valid = validatePath(pathText.getText());
				if (valid) {
					setButtons();
					pathText.setText("");
				}
			}
		});

		resultArea = new JLabel("");
		resultArea.setHorizontalAlignment(SwingConstants.LEFT);
		resultArea.setForeground(Color.BLACK);
		resultArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		resultArea.setBackground(Color.GREEN);
		resultArea.setBounds(34, 168, 473, 150);
		frame.getContentPane().add(resultArea);

		closebtn = new JButton("Close Statement");
		closebtn.setBounds(67, 140, 146, 23);
		closebtn.setEnabled(false);
		frame.getContentPane().add(closebtn);
		closebtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = control.action("CLOSE", queryText.getText());
				if (result.equalsIgnoreCase("Successful")) {
					resultArea.setText("Statement is closed successfully");
				} else {
					resultArea.setText(result);
				}
				queryText.setText("");
			}
		});


		timeOutbtn = new JButton("Set TimeOut");
		timeOutbtn.setEnabled(false);
		timeOutbtn.setBounds(278, 140, 123, 23);
		frame.getContentPane().add(timeOutbtn);
		timeOutbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = control.action("TIMEOUT", queryText.getText());
				if (result.equalsIgnoreCase("Successful")) {
					resultArea.setText("Query TimeOut was set successfully");
				} else {
					resultArea.setText(result);
				}
				queryText.setText("");
			}
		});
	}

	private void setButtons() {
		addPath.setEnabled(false);
		closebtn.setEnabled(true);
		timeOutbtn.setEnabled(true);
		executeBatchbtn.setEnabled(true);
		executeQuerybtn.setEnabled(true);
		clearBatchbtn.setEnabled(true);
		addBatchbtn.setEnabled(true);
	}

	private boolean validatePath(String path) {
		try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
        	resultArea.setText("Invalid Path");
            return false;
        }
		resultArea.setText(control.registerDriver(path));
		if (!resultArea.getText().equals("Connection is successful")) {
			return false;
		}
		return true;
	}


}
