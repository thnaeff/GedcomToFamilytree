/**
 *    Copyright 2014 Thomas Naeff (github.com/thnaeff)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.thn.gedcom.familytree.printer;

/**
 *
 * @author Thomas Naeff (github.com/thnaeff)
 *
 */
public class FamilyTreePrinterUtil {


	/**
	 * This method just repeatedly appends the content of columnContent and returns
	 * the result.
	 * 
	 * @param numberOfColumns
	 * @param columnContent
	 * @return
	 */
	public static String createColumnString(int numberOfColumns, String columnContent) {
		if (numberOfColumns < 0 || columnContent == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder(numberOfColumns * columnContent.length());

		for (int i = 0; i < numberOfColumns; i++) {
			sb.append(columnContent);
		}

		return sb.toString();
	}

}
