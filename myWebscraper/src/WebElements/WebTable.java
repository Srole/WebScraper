package WebElements;

import java.util.ArrayList;
import java.util.Collection;

public class WebTable {
	
	ArrayList<WebTableRow> rows = new ArrayList<>();
	
	public WebTable() {};
	
	public WebTable(WebTableRow...rows) {
		if (rows != null) {
			for (WebTableRow rw : rows) {
				this.addTableRow(rw);
			}
		}
	}
	
	public void addTableRow(WebTableRow row) {
		this.rows.add(row);
	}
	
	public void addTableRows(WebTableRow...rows) {
		for (WebTableRow rw : rows) {
			this.rows.add(rw);
		}
	}
	
	public <T> Collection<WebTableRow> getRows(){
		return new ArrayList<WebTableRow>(this.rows);
	}
	
	public String getHtml() {
		return "<table>\n" + "\n</table>";
	}
	
	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (WebTableRow rw : this.rows) {
			sb.append(rw.getText() + System.lineSeparator());
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
}
