package de.rcblum.stream.deck.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import de.rcblum.stream.deck.StreamDeck;

/**
 * 
 * 
 * <br><br> 
 * 
 * MIT License
 * 
 * Copyright (c) 2017 Roland von Werden
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * @author Roland von Werden
 * @version 0.1
 *
 */
public class IconHelper {

	
	private static Map<String, byte[]> imageCache = new HashMap<>();
	
	static {
		init() ;
	}
	
	private static void init () {
		BufferedImage img = new BufferedImage(StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE);
		g.dispose();
		cacheImage("temp://BLACK_ICON", img);
		img = new BufferedImage(StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, StreamDeck.ICON_SIZE, StreamDeck.ICON_SIZE);
		g.setColor(Color.LIGHT_GRAY);
		g.drawRect(15, 23, 42, 29);
		g.drawRect(16, 24, 40, 27);
		g.drawLine(53, 22, 40, 22);
		g.drawLine(52, 21, 41, 21);
		g.drawLine(51, 20, 42, 20);
		g.drawLine(50, 19, 43, 19);
		g.dispose();
		cacheImage("temp://FOLDER", img);
	}
	
	public static BufferedImage fillBackground(BufferedImage img, Color color) {
		BufferedImage nImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		Graphics2D g = nImg.createGraphics();
		g.setColor(color);
		g.fillRect(0, 0, nImg.getWidth(), nImg.getHeight());
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return nImg;
	}
	
	public static BufferedImage rotate180(BufferedImage inputImage) {
		// The most of code is same as before
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		BufferedImage returnImage = new BufferedImage(height, width, inputImage.getType());
		// We have to change the width and height because when you rotate the
		// image by 90 degree, the
		// width is height and height is width <img
		// src='http://forum.codecall.net/public/style_emoticons/<#EMO_DIR#>/smile.png'
		// class='bbc_emoticon' alt=':)' />

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				returnImage.setRGB(height - x - 1, width - y - 1, inputImage.getRGB(x, y));
				// Again check the Picture for better understanding
			}
		}
		BufferedImage returnImage2 = new BufferedImage(height, width, inputImage.getType());
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				returnImage2.setRGB(x, height - y - 1, returnImage.getRGB(x, y));
				// Again check the Picture for better understanding
			}
		}
		return returnImage2;
	}
	
	public static BufferedImage createResizedCopy(BufferedImage originalImage)
    {
		int scaledWidth = 72;
		int scaledHeight = 72;
		if (originalImage.getWidth() != originalImage.getHeight()) {
			float scalerWidth = 72f/originalImage.getWidth();
			float scalerHeight = 72f/originalImage.getWidth();
			if (scalerWidth < scaledHeight)
				scaledHeight = Math.round(scalerWidth * originalImage.getHeight());
			else
				scaledWidth = Math.round(scalerHeight * originalImage.getWidth());
			
		}
        int imageType = BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (true) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); 
        g.dispose();
        return scaledBI;
    }
	
	public static byte[] loadImage(String path) throws IOException {
		if (imageCache.containsKey(path))
			return imageCache.get(path);
		BufferedImage img = ImageIO.read(new File(path));
		img = IconHelper.createResizedCopy(IconHelper.fillBackground(IconHelper.rotate180(img), Color.BLACK));
		int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		byte[] imgData = new byte[StreamDeck.ICON_SIZE * StreamDeck.ICON_SIZE * 3];
		int imgDataCount=0;
		// remove the alpha channel
		for(int i=0;i< StreamDeck.ICON_SIZE * StreamDeck.ICON_SIZE; i++) {
			//RGB -> BGR
			imgData[imgDataCount++] = (byte)((pixels[i]>>16) & 0xFF);
			imgData[imgDataCount++] = (byte)(pixels[i] & 0xFF);
			imgData[imgDataCount++] = (byte)((pixels[i]>>8) & 0xFF);			
		}
		cache(path, imgData);
		return imgData;
	}

	private static void cache(String path, byte[] imgData) {
		imageCache.put(path, imgData);
		System.out.println("Caching: " + path);
	}

	public static byte[] cacheImage(String path, BufferedImage img) {
		int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		byte[] imgData = new byte[StreamDeck.ICON_SIZE * StreamDeck.ICON_SIZE * 3];
		int imgDataCount=0;
		// remove the alpha channel
		for(int i=0;i< StreamDeck.ICON_SIZE * StreamDeck.ICON_SIZE; i++) {
			//RGB -> BGR
			imgData[imgDataCount++] = (byte)((pixels[i]>>16) & 0xFF);
			imgData[imgDataCount++] = (byte)(pixels[i] & 0xFF);
			imgData[imgDataCount++] = (byte)((pixels[i]>>8) & 0xFF);			
		}
		cache(path, imgData);
		return imgData;
	}

	public static byte[] getImage(String string) {
		if (imageCache == null)
			imageCache = new HashMap<>();
		return imageCache.get(string);
	}
}