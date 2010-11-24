/*
 *                      Nividic development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.eoulsan;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.logging.Logger;

import fr.ens.transcriptome.eoulsan.util.FileUtils;
import fr.ens.transcriptome.eoulsan.util.StringUtils;

/**
 * This class define common constants.
 * @author Laurent Jourdren
 */
public final class Common {

  /** Logger. */
  private static final Logger LOGGER = Logger.getLogger(Globals.APP_NAME);

  public static final String S3_PROTOCOL = "s3n";

  public static final String SOAP_ARGS_DEFAULT = "-r 2 -l 28";

  public static final String SOAP_INPUT_READS_COUNTER = "soap input reads";
  public static final String READS_AFTER_FILTERING_COUNTER =
      "reads after filtering";
  public static final String SOAP_ALIGNEMENT_WITH_ONLY_ONE_HIT_COUNTER =
      "soap alignment with only one hit";

  /**
   * Get the identifier of the sample from the source path
   * @param sample Sample to use
   * @return the identifier of the source
   */
  public static int getSampleId(final String sampleSource) {

    if (sampleSource == null || "".equals(sampleSource)) {
      return -1;
    }

    final String basename = StringUtils.basename(sampleSource);
    final int pos = basename.lastIndexOf('_');

    return Integer.parseInt(basename.substring(pos + 1));
  }

  /**
   * Write log data.
   * @param os OutputStream of the log file
   * @param data data to write
   * @throws IOException if an error occurs while writing log file
   */
  public static void writeLog(final OutputStream os, final long startTime,
      final String data) throws IOException {

    final long endTime = System.currentTimeMillis();
    final long duration = endTime - startTime;

    final Writer writer = new OutputStreamWriter(os);
    writer.write("Start time: "
        + new Date(startTime) + "\nEnd time: " + new Date(endTime)
        + "\nDuration: " + StringUtils.toTimeHumanReadable(duration) + "\n");
    writer.write(data);
    writer.close();
  }

  /**
   * Write log data.
   * @param os OutputStream of the log file
   * @param data data to write
   * @throws IOException if an error occurs while writing log file
   */
  public static void writeLog(final File file, final long startTime,
      final String data) throws IOException {

    if (file == null) {
      throw new NullPointerException("File for log file is null.");
    }

    writeLog(FileUtils.createOutputStream(file), startTime, data);
  }

  /**
   * Show a message and then exit.
   * @param message the message to show
   */
  public static void showMessageAndExit(final String message) {

    System.out.println(message);
    exit(0);
  }

  /**
   * Show and log an error message.
   * @param message message to show and log
   */
  public static void showAndLogErrorMessage(final String message) {

    LOGGER.severe(message);
    System.err.println(message);
  }

  /**
   * Show a message and then exit.
   * @param message the message to show
   */
  public static void showErrorMessageAndExit(final String message) {

    System.err.println(message);
    exit(1);
  }

  /**
   * Print error message to the user and exits the application.
   * @param e Exception
   * @param message message to show to the use
   */
  public static void errorExit(final Exception e, final String message) {

    errorExit(e, message, true);
  }

  /**
   * Print error message to the user and exits the application.
   * @param e Exception
   * @param message message to show to the use
   * @param logMessage true if message must be logged
   */
  public static void errorExit(final Exception e, final String message,
      final boolean logMessage) {

    if (logMessage) {
      LOGGER.severe(message);
    }

    System.err.println("\n=== " + Globals.APP_NAME + " Error ===");
    System.err.println(message);

    if (!EoulsanRuntime.isRuntime()
        || EoulsanRuntime.getSettings().isPrintStackTrace()) {
      printStackTrace(e);
    }

    exit(1);
  }

  /**
   * Print the stack trace for an exception.
   * @param e Exception
   */
  private static void printStackTrace(final Exception e) {

    System.err.println("\n=== " + Globals.APP_NAME + " Debug Stack Trace ===");
    e.printStackTrace();
    System.err.println();
  }

  /**
   * Exit the application.
   * @param exitCode exit code
   */
  public static void exit(final int exitCode) {

    System.exit(exitCode);
  }

  //
  // Constructor
  //

  private Common() {

    throw new IllegalStateException();
  }

}
