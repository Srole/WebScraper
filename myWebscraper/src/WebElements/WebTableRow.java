package WebElements;

import java.util.ArrayList;

public class WebTableRow {

	ArrayList<WebTableData> data = new ArrayList<>();
	
	public void addCell(WebTableData td) {
		this.data.add(td);
	}
	
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<tr>");
		data.forEach(x -> {
			sb.append(x.getHtml());
		});
		sb.append("<tr>");
		return sb.toString();
	}
	
	public String getText() {
		StringBuilder sb = new StringBuilder();
		
		for (WebTableData td : data) {
			sb.append(td.getText() + " ");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
