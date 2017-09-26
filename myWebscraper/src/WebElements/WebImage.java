package WebElements;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

public class WebImage {

	String imageName = "";
	String base64img = "";
	
	public WebImage(String base64, String name) {this.base64img = base64;}
	public WebImage(BufferedImage img, String imageName) {
		this.base64img = imageToString(img);
		this.imageName = imageName;
	}
	
	public String getBase64String() {
		return this.base64img;
	}
	
	public BufferedImage getBufferedImage() {
		return stringToImage(this.base64img);
	}
	
	public static String imageToString(BufferedImage img) {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			ImageIO.write(img, "gif", bos);
			return Base64.getEncoder().encodeToString(bos.toByteArray());
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static BufferedImage stringToImage(String imgString) {
		BufferedImage img = null;
		byte[] imgByte = Base64.getDecoder().decode(imgString);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(imgByte);
		try {
		img = ImageIO.read(bis);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return img;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}
