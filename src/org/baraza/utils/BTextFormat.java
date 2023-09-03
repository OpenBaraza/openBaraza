/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;

import org.apache.commons.text.StringEscapeUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;
import org.jsoup.parser.Parser;

public class BTextFormat {

	Logger log = Logger.getLogger(BTextFormat.class.getName());
	private static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
	private static final Pattern pattern = Pattern.compile(HTML_PATTERN);

	public static String htmlToText(String htmlData) {
		String textData = "";
		Matcher matcher = pattern.matcher(htmlData);
 
		if(matcher.find()) {
			org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(htmlData.trim());
			jsoupDoc.outputSettings(new OutputSettings().prettyPrint(false));
			jsoupDoc.select("br").after("\\n");
			jsoupDoc.select("p").before("\\n");
			
			String strDoc = jsoupDoc.html().replaceAll("\\\\n", "\n");
			textData = Jsoup.clean(strDoc, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
			textData = StringEscapeUtils.unescapeHtml4(textData);
		} else {
			textData = htmlData;
		}
		
		return textData;
	}
	
}
