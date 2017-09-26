package WebElements;

public class WebTableData {

	private String text = "";
	
	public WebTableData(String text) {
	this.text = text;	
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getHtml() {
		return "<td>"+this.text+"</td>";
	}
	
	public static void main(String[] args) {
		
	}

}
