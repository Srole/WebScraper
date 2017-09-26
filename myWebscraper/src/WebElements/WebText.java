/**
 * 
 */
package WebElements;

/**
 * @author slp
 *
 */
public class WebText {

	
	String text = "ABC		  A A A C \n  Q Q QQQ";
	
	/**
	 * @param args
	 */
	
	public String getPlainText() {
		return text.replaceAll("[\\t\\n ]", "");
	}
	
	public static void main(String[] args) {
		WebText wt = new WebText();
		System.out.println(wt.getPlainText());
	}

}
