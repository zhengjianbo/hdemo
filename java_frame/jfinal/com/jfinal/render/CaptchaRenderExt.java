package com.jfinal.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;

import com.jfinal.kit.LogKit;

/**
 * CaptchaRender. 验证码
 */
public class CaptchaRenderExt extends Render {
 
	// 榛樿鐨勯獙璇佺爜澶у皬
	private static final int WIDTH = 108, HEIGHT = 40;
	// 楠岃瘉鐮侀殢鏈哄瓧绗︽暟缁�
	public static final String[] strArr = {"3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y"};
	
	// 楠岃瘉鐮佸瓧浣�
	private static final Font[] RANDOM_FONT = new Font[] {
			new Font("nyala", Font.BOLD, 38), new Font("Arial", Font.BOLD, 32),
			new Font("Bell MT", Font.BOLD, 32),
			new Font("Credit valley", Font.BOLD, 34),
			new Font("Impact", Font.BOLD, 32),
			new Font(Font.MONOSPACED, Font.BOLD, 40) };

	public String random = null;

	public void setRnd(String random) {
		this.random = random; 
	}

	/**
	 *  
	 */
	public void render() {
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		drawGraphic(random, image);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		ServletOutputStream sos = null;
		try {
			sos = response.getOutputStream();
			ImageIO.write(image, "jpeg", sos);
		} catch (IOException e) {
			if (getDevMode()) {
				throw new RenderException(e);
			}
		} catch (Exception e) {
			throw new RenderException(e);
		} finally {
			if (sos != null) {
				try {
					sos.close();
				} catch (IOException e) {
					LogKit.logNothing(e);
				}
			}
		}
	}

	private String drawGraphic(String randomStr, BufferedImage image) {
		// 鑾峰彇鍥惧舰涓婁笅鏂�
		Graphics2D g = image.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		// 鍥惧舰鎶楅敮榻�
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// 瀛椾綋鎶楅敮榻�
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// 璁惧畾鑳屾櫙鑹�
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		// 鐢熸垚闅忔満绫�
		Random random = new Random();
		// 璁惧畾瀛椾綋
		g.setFont(RANDOM_FONT[random.nextInt(RANDOM_FONT.length)]);

		// 鐢昏泲铔嬶紝鏈夎泲鐨勭敓娲绘墠绮惧僵
		Color color;
		for (int i = 0; i < 10; i++) {
			color = getRandColor(120, 200);
			g.setColor(color);
			g.drawOval(random.nextInt(WIDTH), random.nextInt(HEIGHT),
					5 + random.nextInt(10), 5 + random.nextInt(10));
			color = null;
		}
 
		String sRand = "";
		for (int i = 0; i < randomStr.length(); i++) {
			String rand = randomStr.substring(i,i+1);
			sRand += rand; 
			int degree = random.nextInt(28);
			if (i % 2 == 0) {
				degree = degree * (-1);
			}
			// 瀹氫箟鍧愭爣
			int x = 22 * i, y = 21;
			// 鏃嬭浆鍖哄煙
			g.rotate(Math.toRadians(degree), x, y);
			// 璁惧畾瀛椾綋棰滆壊
			color = getRandColor(20, 130);
			g.setColor(color);
			// 灏嗚璇佺爜鏄剧ず鍒板浘璞′腑
			g.drawString(rand, x + 8, y + 10);
			// 鏃嬭浆涔嬪悗锛屽繀椤绘棆杞洖鏉�
			g.rotate(-Math.toRadians(degree), x, y);
			color = null;
		}
		// 鍥剧墖涓棿绾�
		g.setColor(getRandColor(0, 60));
		// width鏄嚎瀹�,float鍨�
		BasicStroke bs = new BasicStroke(3);
		g.setStroke(bs);
		// 鐢诲嚭鏇茬嚎
		QuadCurve2D.Double curve = new QuadCurve2D.Double(0d,
				random.nextInt(HEIGHT - 8) + 4, WIDTH / 2, HEIGHT / 2, WIDTH,
				random.nextInt(HEIGHT - 8) + 4);
		g.draw(curve);
		// 閿�姣佸浘鍍�
		g.dispose();
		return sRand;
	}

	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

}
