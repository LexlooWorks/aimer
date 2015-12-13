package com.nvapp.comic.lib.util;

public class StringUtils {
	/**
	 * 判断字符窜是否不为null和空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static String getDownloadFileName(String audio_url) {// 获取下载的音频文件名�?
		int lastPosition = audio_url.lastIndexOf('/');
		String audio_name = audio_url.substring(lastPosition, audio_url.length());
		return audio_name;
	}

	public static String join(String join, String[] strAry) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strAry.length; i++) {
			if (i == (strAry.length - 1)) {
				sb.append(strAry[i]);
			} else {
				sb.append(strAry[i]).append(join);
			}
		}

		return new String(sb);
	}
}
