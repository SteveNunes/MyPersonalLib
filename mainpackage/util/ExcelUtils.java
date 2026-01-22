package util;

import java.awt.Color;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class ExcelUtils {

	public static Sheet createSheet(Workbook workbook, String sheetName) {
		if (workbook == null)
			throw new IllegalArgumentException("Workbook não pode ser null");

		if (sheetName == null || sheetName.isBlank())
			return workbook.createSheet();

		return workbook.createSheet(sheetName);
	}

	public static Sheet getOrCreateSheet(Workbook workbook, String sheetName) {
		if (workbook == null)
			throw new IllegalArgumentException("Workbook não pode ser null");

		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet != null)
			return sheet;

		return workbook.createSheet(sheetName);
	}

	public static void removeSheet(Workbook workbook, int sheetIndex) {
		if (workbook == null)
			throw new IllegalArgumentException("Workbook não pode ser null");

		if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets())
			throw new IllegalArgumentException("Índice de planilha inválido: " + sheetIndex);

		workbook.removeSheetAt(sheetIndex);
	}

	public static void removeSheet(Workbook workbook, String sheetName) {
		if (workbook == null)
			throw new IllegalArgumentException("Workbook não pode ser null");

		if (sheetName == null || sheetName.isBlank())
			return;

		int index = workbook.getSheetIndex(sheetName);
		if (index >= 0)
			workbook.removeSheetAt(index);
	}

	public static void renameSheet(Workbook workbook, String oldName, String newName) {
		if (workbook == null)
			throw new IllegalArgumentException("Workbook não pode ser null");

		if (oldName == null || newName == null || newName.isBlank())
			throw new IllegalArgumentException("Nome de planilha inválido");

		int index = workbook.getSheetIndex(oldName);
		if (index < 0)
			throw new IllegalArgumentException("Planilha não encontrada: " + oldName);

		workbook.setSheetName(index, newName);
	}

	public static void moveSheet(Workbook workbook, String sheetName, int newIndex) {
		if (workbook == null)
			throw new IllegalArgumentException("Workbook não pode ser null");

		int index = workbook.getSheetIndex(sheetName);
		if (index < 0)
			throw new IllegalArgumentException("Planilha não encontrada: " + sheetName);

		if (newIndex < 0 || newIndex >= workbook.getNumberOfSheets())
			throw new IllegalArgumentException("Índice de destino inválido");

		workbook.setSheetOrder(sheetName, newIndex);
	}

	public static void setSheetHidden(Workbook workbook, String sheetName, boolean hidden) {
		if (workbook == null)
			throw new IllegalArgumentException("Workbook não pode ser null");

		int index = workbook.getSheetIndex(sheetName);
		if (index < 0)
			throw new IllegalArgumentException("Planilha não encontrada: " + sheetName);

		workbook.setSheetHidden(index, hidden);
	}

	/*
	 * ========================================================= GETTERS BASE (NUNCA
	 * RETORNAM NULL) =========================================================
	 */

	public static Row getRow(Sheet sheet, int lineIndex) {
		if (sheet == null)
			throw new IllegalArgumentException("Sheet não pode ser null");

		Row row = sheet.getRow(lineIndex);
		if (row == null)
			row = sheet.createRow(lineIndex);

		return row;
	}

	public static Cell getCell(Sheet sheet, int lineIndex, int colIndex) {
		return getRow(sheet, lineIndex).getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
	}

	/*
	 * ========================================================= LEITURA SEGURA
	 * =========================================================
	 */

	public static Object getCellValue(Sheet sheet, int line, int col) {
		Cell cell = getCell(sheet, line, col);

		return switch (cell.getCellType()) {
			case STRING -> cell.getStringCellValue();
			case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
			case BOOLEAN -> cell.getBooleanCellValue();
			case FORMULA -> cell.getCellFormula();
			case ERROR -> cell.getErrorCellValue();
			case BLANK, _NONE -> null;
		};
	}

	public static String getCellString(Sheet sheet, int line, int col) {
		Object v = getCellValue(sheet, line, col);
		return v == null ? null : v.toString();
	}

	public static Object getCellDisplayValue(Sheet sheet, int lineIndex, int colIndex) {
		if (sheet == null)
			throw new IllegalArgumentException("Sheet não pode ser null");

		Cell cell = getCell(sheet, lineIndex, colIndex);

		Workbook workbook = sheet.getWorkbook();
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		CellValue evaluated = evaluator.evaluate(cell);
		if (evaluated == null)
			return null;

		return switch (evaluated.getCellType()) {
			case STRING -> evaluated.getStringValue();
			case NUMERIC -> evaluated.getNumberValue();
			case BOOLEAN -> evaluated.getBooleanValue();
			case BLANK, _NONE -> null;
			case ERROR -> evaluated.getErrorValue();
			default -> null;
		};
	}

	/*
	 * ========================================================= LIMPEZA
	 * =========================================================
	 */

	public static void clearCell(Sheet sheet, int line, int col) {
		getCell(sheet, line, col).setBlank();
	}

	public static void clearCells(Sheet sheet, int startLine, int endLine, int startCol, int endCol) {
		for (int r = startLine; r <= endLine; r++)
			for (int c = startCol; c <= endCol; c++)
				clearCell(sheet, r, c);
	}

	public static void clearRow(Sheet sheet, int line) {
		Row row = getRow(sheet, line);
		for (int c = 0; c < row.getLastCellNum(); c++)
			clearCell(sheet, line, c);
	}

	public static void clearColumn(Sheet sheet, int col) {
		for (int r = 0; r <= sheet.getLastRowNum(); r++)
			clearCell(sheet, r, col);
	}

	/*
	 * ========================================================= TEXTO
	 * =========================================================
	 */

	public static void editCellText(Sheet sheet, int line, int col, String text) {
		getCell(sheet, line, col).setCellValue(text);
	}

	/*
	 * ========================================================= FONT
	 * =========================================================
	 */

	public static Font createFont(Sheet sheet, String fontName, int fontSize, boolean bold, boolean italic, boolean underlined, IndexedColors fontColor) {
		Font font = sheet.getWorkbook().createFont();

		if (fontName != null && !fontName.isBlank())
			font.setFontName(fontName);

		if (fontSize > 0)
			font.setFontHeightInPoints((short) fontSize);

		font.setBold(bold);
		font.setItalic(italic);

		if (underlined)
			font.setUnderline(Font.U_SINGLE);

		if (fontColor != null)
			font.setColor(fontColor.getIndex());

		return font;
	}

	public static void editCellFont(Sheet sheet, int line, int col, Font font) {
		Cell cell = getCell(sheet, line, col);

		CellStyle style = sheet.getWorkbook().createCellStyle();
		style.cloneStyleFrom(cell.getCellStyle());
		style.setFont(font);

		cell.setCellStyle(style);
	}

	/*
	 * ========================================================= CORES
	 * =========================================================
	 */

	private static void setCellColorPrivate(Sheet sheet, int startLineIndex, int endLineIndex, int startColIndex, int endColIndex, IndexedColors indexedColor, int r, int g, int b) {
		if (sheet == null)
			throw new IllegalArgumentException("Sheet não pode ser null");

		if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
			throw new IllegalArgumentException("RGB fora do intervalo 0–255");

		if (startLineIndex > endLineIndex || startColIndex > endColIndex)
			throw new IllegalArgumentException("Intervalo inválido");

		Workbook workbook = sheet.getWorkbook();

		for (int rIdx = startLineIndex; rIdx <= endLineIndex; rIdx++) {
			for (int cIdx = startColIndex; cIdx <= endColIndex; cIdx++) {

				Cell cell = getCell(sheet, rIdx, cIdx);
				CellStyle style = workbook.createCellStyle();
				style.cloneStyleFrom(cell.getCellStyle());

				if (indexedColor != null) {
					style.setFillForegroundColor(indexedColor.getIndex());
					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cell.setCellStyle(style);
				}
				else {
					XSSFCellStyle xssfStyle = (XSSFCellStyle)style;
					XSSFColor color = new XSSFColor(new Color(r, g, b), new DefaultIndexedColorMap());
					xssfStyle.setFillForegroundColor(color);
					xssfStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cell.setCellStyle(xssfStyle);
				}
			}
		}
	}

	public static void setCellColor(Sheet sheet, int startLineIndex, int endLineIndex, int startColIndex, int endColIndex, int r, int g, int b) {
		setCellColorPrivate(sheet, startLineIndex, endLineIndex, startColIndex, endColIndex, null, r, g, b);
	}
	
	public static void setCellColor(Sheet sheet, int lineIndex, int colIndex, int r, int g, int b) {
		setCellColorPrivate(sheet, lineIndex, lineIndex, colIndex, colIndex, null, r, g, b);
	}

	public static void setCellColor(Sheet sheet, int startLineIndex, int endLineIndex, int startColIndex, int endColIndex, String hexColor) {
		if (hexColor == null)
			throw new IllegalArgumentException("Hex color não pode ser null");

		String hex = hexColor.trim();
		if (hex.startsWith("#"))
			hex = hex.substring(1);

		if (!hex.matches("[0-9a-fA-F]{6}"))
			throw new IllegalArgumentException("Hex color inválido (use RRGGBB): " + hexColor);

		int r = Integer.parseInt(hex.substring(0, 2), 16);
		int g = Integer.parseInt(hex.substring(2, 4), 16);
		int b = Integer.parseInt(hex.substring(4, 6), 16);

		setCellColorPrivate(sheet, startLineIndex, endLineIndex, startColIndex, endColIndex, null, r, g, b);
	}

	public static void setCellColor(Sheet sheet, int lineIndex, int colIndex, String hexColor) {
		setCellColor(sheet, lineIndex, lineIndex, colIndex, colIndex, hexColor);
	}

	public static void setCellColor(Sheet sheet, int startLineIndex, int endLineIndex, int startColIndex, int endColIndex, IndexedColors indexedColor) {
		setCellColorPrivate(sheet, startLineIndex, endLineIndex, startColIndex, endColIndex, indexedColor, 0, 0, 0);
	}

	public static void setCellColor(Sheet sheet, int lineIndex, int colIndex, IndexedColors indexedColor) {
		setCellColor(sheet, lineIndex, lineIndex, colIndex, colIndex, indexedColor);
	}

	/*
	 * ========================================================= ALINHAMENTO
	 * =========================================================
	 */

	public static void setCellAlignment(Sheet sheet, int startLine, int endLine, int startCol, int endCol, HorizontalAlignment hAlign, VerticalAlignment vAlign) {
		for (int r = startLine; r <= endLine; r++)
			for (int c = startCol; c <= endCol; c++) {

				Cell cell = getCell(sheet, r, c);

				CellStyle style = sheet.getWorkbook().createCellStyle();
				style.cloneStyleFrom(cell.getCellStyle());

				if (hAlign != null)
					style.setAlignment(hAlign);
				if (vAlign != null)
					style.setVerticalAlignment(vAlign);

				cell.setCellStyle(style);
			}
	}

	public static void setCellAlignment(Sheet sheet, int line, int col, HorizontalAlignment hAlign, VerticalAlignment vAlign) {
		setCellAlignment(sheet, line, line, col, col, hAlign, vAlign);
	}

	/*
	 * ========================================================= BORDAS
	 * =========================================================
	 */

	public static void setCellBorder(Sheet sheet, int startLine, int endLine, int startCol, int endCol, BorderStyle style) {
		for (int r = startLine; r <= endLine; r++)
			for (int c = startCol; c <= endCol; c++) {

				Cell cell = getCell(sheet, r, c);

				CellStyle cs = sheet.getWorkbook().createCellStyle();
				cs.cloneStyleFrom(cell.getCellStyle());

				cs.setBorderTop(style);
				cs.setBorderBottom(style);
				cs.setBorderLeft(style);
				cs.setBorderRight(style);

				cell.setCellStyle(cs);
			}
	}

	public static void setCellBorder(Sheet sheet, int line, int col, BorderStyle style) {
		setCellBorder(sheet, line, line, col, col, style);
	}

	/*
	 * ========================================================= AUTO SIZE
	 * =========================================================
	 */

	public static void autoSizeColumn(Sheet sheet, int col) {
		sheet.autoSizeColumn(col);
	}

	public static void autoSizeColumns(Sheet sheet, int startCol, int endCol) {
		for (int c = startCol; c <= endCol; c++)
			autoSizeColumn(sheet, c);
	}

	/*
	 * ========================================================= MESCLAGEM
	 * =========================================================
	 */

	public static void mergeCells(Sheet sheet, int startLine, int endLine, int startCol, int endCol) {
		sheet.addMergedRegion(new CellRangeAddress(startLine, endLine, startCol, endCol));
	}

	/*
	 * ========================================================= DIMENSÕES
	 * =========================================================
	 */

	public static void setColumnWidth(Sheet sheet, int startCol, int endCol, int size) {
		for (int c = startCol; c <= endCol; c++)
			sheet.setColumnWidth(c, size);
	}

	public static void setRowHeight(Sheet sheet, int startLine, int endLine, float size) {
		for (int r = startLine; r <= endLine; r++)
			getRow(sheet, r).setHeightInPoints(size);
	}

	/*
	 * ========================================================= CRIAÇÃO EM MASSA
	 * =========================================================
	 */

	public static void massCreateLinesWithCells(Sheet sheet, int totalLines, int totalCells) {
		if (sheet == null)
			throw new IllegalArgumentException("Sheet não pode ser null");

		for (int linha = 0; linha < totalLines; linha++) {

			Row row = getRow(sheet, linha);
			if (row == null) {
				insertNewLine(sheet, linha);
				row = getRow(sheet, linha);
			}

			for (int cell = 0; cell < totalCells; cell++) {
				if (getCell(sheet, linha, cell) == null)
					row.createCell(cell);
			}
		}
	}

	/*
	 * ========================================================= DELETE DE LINHAS
	 * =========================================================
	 */

	public static void deleteLine(Sheet sheet, int lineIndex) {
		deleteLine(sheet, lineIndex, lineIndex);
	}

	public static void deleteLine(Sheet sheet, int startLineIndex, int endLineIndex) {
		if (sheet == null)
			throw new IllegalArgumentException("Sheet não pode ser null");

		if (startLineIndex > endLineIndex)
			throw new IllegalArgumentException("Índice inicial maior que o final");

		int total = endLineIndex - startLineIndex + 1;
		int lastRow = sheet.getLastRowNum();

		for (int i = startLineIndex; i <= endLineIndex; i++) {
			Row row = sheet.getRow(i);
			if (row != null)
				sheet.removeRow(row);
		}

		if (endLineIndex < lastRow)
			sheet.shiftRows(endLineIndex + 1, lastRow, -total);
	}

	/*
	 * ========================================================= INSERT / COPY DE
	 * LINHAS =========================================================
	 */

	public static void insertNewLine(Sheet sheet, int lineIndex) {
		if (sheet == null)
			throw new IllegalArgumentException("Sheet não pode ser null");

		Row referenceRow = sheet.getRow(lineIndex);
		if (referenceRow == null)
			throw new IllegalArgumentException("Linha de referência não existe: " + lineIndex);

		int insertIndex = lineIndex + 1;
		int lastRow = sheet.getLastRowNum();

		sheet.shiftRows(insertIndex, lastRow, 1, true, false);

		Row newRow = sheet.createRow(insertIndex);
		newRow.setHeight(referenceRow.getHeight());

		for (int c = 0; c < referenceRow.getLastCellNum(); c++) {
			Cell refCell = referenceRow.getCell(c);
			if (refCell == null)
				continue;

			Cell newCell = newRow.createCell(c);

			CellStyle style = sheet.getWorkbook().createCellStyle();
			style.cloneStyleFrom(refCell.getCellStyle());

			newCell.setCellStyle(style);
		}
	}

	public static void copyLine(Sheet sheet, int sourceIndex, int targetIndex) {
		copyLine(sheet, sourceIndex, sourceIndex, targetIndex);
	}

	public static void copyLine(Sheet sheet, int sourceStartIndex, int sourceEndIndex, int targetStartIndex) {
		if (sheet == null)
			throw new IllegalArgumentException("Sheet não pode ser null");

		if (sourceStartIndex > sourceEndIndex)
			throw new IllegalArgumentException("Intervalo de origem inválido");

		int blockSize = sourceEndIndex - sourceStartIndex + 1;
		int lastRow = sheet.getLastRowNum();

		sheet.shiftRows(targetStartIndex, lastRow, blockSize, true, false);

		for (int offset = 0; offset < blockSize; offset++) {

			Row sourceRow = sheet.getRow(sourceStartIndex + offset);
			if (sourceRow == null)
				continue;

			insertNewLine(sheet, targetStartIndex + offset - 1);

			Row targetRow = sheet.getRow(targetStartIndex + offset);
			targetRow.setHeight(sourceRow.getHeight());

			for (int c = 0; c < sourceRow.getLastCellNum(); c++) {

				Cell sourceCell = sourceRow.getCell(c);
				if (sourceCell == null)
					continue;

				Cell targetCell = getCell(sheet, targetRow.getRowNum(), c);

				switch (sourceCell.getCellType()) {

					case STRING -> targetCell.setCellValue(sourceCell.getStringCellValue());

					case NUMERIC -> targetCell.setCellValue(sourceCell.getNumericCellValue());

					case BOOLEAN -> targetCell.setCellValue(sourceCell.getBooleanCellValue());

					case FORMULA -> targetCell.setCellFormula(sourceCell.getCellFormula());

					case ERROR -> targetCell.setCellErrorValue(sourceCell.getErrorCellValue());

					case BLANK, _NONE -> {
						// permanece em branco
					}
				}
			}
		}
	}

	public static void setColumnWidth(Sheet sheet, int colIndex, int size) {
		setColumnWidth(sheet, colIndex, colIndex, size);
	}

	public static void setRowHeight(Sheet sheet, int lineIndex, float size) {
		setRowHeight(sheet, lineIndex, lineIndex, size);
	}

}
