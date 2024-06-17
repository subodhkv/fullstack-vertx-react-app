package com.curd.vertx_app;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilenameProcessor {
  public static void main(String[] args) {
    String filename = "20240606.5.note.txt.zip";

    // Extract and validate the date, then find the number after it
    extractDateAndNumber(filename);
  }

  public static void extractDateAndNumber(String filename) {
    // Define regex to find date in yyyymmdd format and capture the number after it
    Pattern pattern = Pattern.compile("(\\d{8})\\.(\\d+)");
    Matcher matcher = pattern.matcher(filename);

    if (matcher.find()) {
      String datePart = matcher.group(1);
      String numberAfterDate = matcher.group(2);

      // Validate the date format
      if (isValidDate(datePart)) {
        System.out.println("The date " + datePart + " is in the correct format.");
        System.out.println("The number immediately after the date is: " + numberAfterDate);
      } else {
        System.out.println("The date " + datePart + " is NOT in the correct format.");
      }
    } else {
      System.out.println("No valid date and number sequence found in the filename.");
    }
  }

  public static boolean isValidDate(String dateStr) {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    try {
      LocalDate.parse(dateStr, dateFormatter);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }
}
