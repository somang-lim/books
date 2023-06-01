package com.books.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class Ut {
	public static String getTempPassword(int length) {
		UUID uuid = UUID.randomUUID();
		String tempPassword = uuid.toString().substring(0, length);

		return tempPassword;
	}

	public static class url {
		public static boolean isUrl(String url) {
			if (url == null)
				return false;
			return url.matches(
				"^(https?):\\/\\/([^:\\/\\s]+)(:([^\\/]*))?((\\/[^\\s/\\/]+)*)?\\/?([^#\\s\\?]*)(\\?([^#\\s]*))?(#(\\w*))?$");
		}

		public static String addQueryParam(String url, String paramName, String paramValue) {
			if (!url.contains("?")) {
				url += "?";
			}

			if (!url.endsWith("?") && !url.endsWith("&")) {
				url += "&";
			}

			url += paramName + "=" + paramValue;

			return url;
		}

		public static String modifyQueryParam(String url, String paramName, String paramValue) {
			url = deleteQueryParam(url, paramName);
			url = addQueryParam(url, paramName, paramValue);

			return url;
		}

		private static String deleteQueryParam(String url, String paramName) {
			int startPoint = url.indexOf(paramName + "=");
			if (startPoint == -1)
				return url;

			int endPoint = url.substring(startPoint).indexOf("&");

			if (endPoint == -1) {
				return url.substring(0, startPoint - 1);
			}

			String urlAfter = url.substring(startPoint + endPoint + 1);

			return url.substring(0, startPoint) + urlAfter;
		}

		public static String encode(String str) {
			try {
				return URLEncoder.encode(str, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				return str;
			}
		}
	}

	public String nf(long number) {
		return String.format("%d", (int) number);
	}
}
