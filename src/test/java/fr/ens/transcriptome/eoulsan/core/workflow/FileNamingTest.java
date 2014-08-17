/*
 *                  Eoulsan development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License version 2.1 or
 * later and CeCILL-C. This should be distributed with the code.
 * If you do not have a copy, see:
 *
 *      http://www.gnu.org/licenses/lgpl-2.1.txt
 *      http://www.cecill.info/licences/Licence_CeCILL-C_V1-en.txt
 *
 * Copyright for this code is held jointly by the Genomic platform
 * of the Institut de Biologie de l'École Normale Supérieure and
 * the individual authors. These should be listed in @author doc
 * comments.
 *
 * For more information on the Eoulsan project and its aims,
 * or to join the Eoulsan Google group, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/eoulsan
 *
 */

package fr.ens.transcriptome.eoulsan.core.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.ens.transcriptome.eoulsan.EoulsanException;
import fr.ens.transcriptome.eoulsan.EoulsanRuntimeDebug;
import fr.ens.transcriptome.eoulsan.data.DataFile;
import fr.ens.transcriptome.eoulsan.data.DataFormats;
import fr.ens.transcriptome.eoulsan.io.CompressionType;

public class FileNamingTest {

  @Before
  public void setUp() {

    try {
      EoulsanRuntimeDebug.initDebugEoulsanRuntime();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (EoulsanException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetStepId() {

    assertEquals("filterreads",
        FileNaming.parse("filterreads_output_reads_s1_file0.fq").getStepId());
    assertEquals("filterreads",
        FileNaming.parse("filterreads_output_reads_s1_file0_part1.fq")
            .getStepId());
    assertEquals("genericindexgenerator",
        FileNaming.parse("genericindexgenerator_output_bowtieindex_genome.zip")
            .getStepId());

    assertEquals("filterreads",
        FileNaming.parse("filterreads_output_reads_s1_file0.fq.bz2")
            .getStepId());
    assertEquals("filterreads",
        FileNaming.parse("filterreads_output_reads_s1_file0_part1.fq.bz2")
            .getStepId());
    assertEquals(
        "genericindexgenerator",
        FileNaming.parse(
            "genericindexgenerator_output_bowtieindex_genome.zip.bz2")
            .getStepId());
  }

  @Test
  public void testGetPortName() {

    assertEquals("output",
        FileNaming.parse("filterreads_output_reads_s1_file0.fq").getPortName());
    assertEquals("output",
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq")
            .getPortName());
    assertEquals("output2",
        FileNaming
            .parse("genericindexgenerator_output2_bowtieindex_genome.zip")
            .getPortName());

    assertEquals("output",
        FileNaming.parse("filterreads_output_reads_s1_file0.fq.bz2")
            .getPortName());
    assertEquals("output",
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq.bz2")
            .getPortName());
    assertEquals(
        "output2",
        FileNaming.parse(
            "genericindexgenerator_output2_bowtieindex_genome.zip.bz2")
            .getPortName());
  }

  @Test
  public void testGetDataName() {

    assertEquals("s1", FileNaming.parse("filterreads_output_reads_s1_file0.fq")
        .getDataName());
    assertEquals("s2",
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq")
            .getDataName());
    assertEquals("genome",
        FileNaming.parse("genericindexgenerator_output_bowtieindex_genome.zip")
            .getDataName());

    assertEquals("s1",
        FileNaming.parse("filterreads_output_reads_s1_file0.fq.bz2")
            .getDataName());
    assertEquals("s2",
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq.bz2")
            .getDataName());
    assertEquals(
        "genome",
        FileNaming.parse(
            "genericindexgenerator_output_bowtieindex_genome.zip.bz2")
            .getDataName());
  }

  @Test
  public void testGetFormat() {

    assertEquals(DataFormats.READS_FASTQ,
        FileNaming.parse("filterreads_output_reads_s1_file0.fq").getFormat());
    assertEquals(DataFormats.READS_FASTQ,
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq")
            .getFormat());
    assertEquals(DataFormats.BOWTIE_INDEX_ZIP,
        FileNaming.parse("genericindexgenerator_output_bowtieindex_genome.zip")
            .getFormat());

    assertEquals(DataFormats.READS_FASTQ,
        FileNaming.parse("filterreads_output_reads_s1_file0.fq.bz2")
            .getFormat());
    assertEquals(DataFormats.READS_FASTQ,
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq.bz2")
            .getFormat());
    assertEquals(
        DataFormats.BOWTIE_INDEX_ZIP,
        FileNaming.parse(
            "genericindexgenerator_output_bowtieindex_genome.zip.bz2")
            .getFormat());
  }

  @Test
  public void testGetFileIndex() {
    assertEquals(0, FileNaming.parse("filterreads_output_reads_s1_file0.fq")
        .getFileIndex());
    assertEquals(1, FileNaming.parse("filterreads_output_reads_s1_file1.fq")
        .getFileIndex());
    assertEquals(0,
        FileNaming.parse("filterreads_output_reads_s2_file0_part3.fq")
            .getFileIndex());
    assertEquals(1,
        FileNaming.parse("filterreads_output_reads_s2_file1_part4.fq")
            .getFileIndex());
    assertEquals(-1,
        FileNaming.parse("genericindexgenerator_output_bowtieindex_genome.zip")
            .getFileIndex());

    assertEquals(0, FileNaming
        .parse("filterreads_output_reads_s1_file0.fq.bz2").getFileIndex());
    assertEquals(1, FileNaming
        .parse("filterreads_output_reads_s1_file1.fq.bz2").getFileIndex());
    assertEquals(0,
        FileNaming.parse("filterreads_output_reads_s2_file0_part3.fq.bz2")
            .getFileIndex());
    assertEquals(1,
        FileNaming.parse("filterreads_output_reads_s2_file1_part4.fq.bz2")
            .getFileIndex());
    assertEquals(
        -1,
        FileNaming.parse(
            "genericindexgenerator_output_bowtieindex_genome.zip.bz2")
            .getFileIndex());
  }

  @Test
  public void testGetPart() {
    assertEquals(-1, FileNaming.parse("filterreads_output_reads_s1_file0.fq")
        .getPart());
    assertEquals(1,
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq")
            .getPart());
    assertEquals(-1,
        FileNaming.parse("genericindexgenerator_output_bowtieindex_genome.zip")
            .getPart());
    assertEquals(
        1,
        FileNaming.parse(
            "genericindexgenerator_output_bowtieindex_genome_part1.zip")
            .getPart());

    assertEquals(-1,
        FileNaming.parse("filterreads_output_reads_s1_file0.fq.bz2").getPart());
    assertEquals(1,
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq.bz2")
            .getPart());
    assertEquals(
        -1,
        FileNaming.parse(
            "genericindexgenerator_output_bowtieindex_genome.zip.bz2")
            .getPart());
    assertEquals(
        1,
        FileNaming.parse(
            "genericindexgenerator_output_bowtieindex_genome_part1.zip.bz2")
            .getPart());
  }

  @Test
  public void testGetCompression() {

    assertEquals(CompressionType.NONE,
        FileNaming.parse("filterreads_output_reads_s1_file0.fq")
            .getCompression());
    assertEquals(CompressionType.NONE,
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq")
            .getCompression());
    assertEquals(CompressionType.NONE,
        FileNaming.parse("genericindexgenerator_output_bowtieindex_genome.zip")
            .getCompression());

    assertEquals(CompressionType.BZIP2,
        FileNaming.parse("filterreads_output_reads_s1_file0.fq.bz2")
            .getCompression());
    assertEquals(CompressionType.BZIP2,
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq.bz2")
            .getCompression());
    assertEquals(
        CompressionType.BZIP2,
        FileNaming.parse(
            "genericindexgenerator_output_bowtieindex_genome.zip.bz2")
            .getCompression());

    assertEquals(CompressionType.GZIP,
        FileNaming.parse("filterreads_output_reads_s1_file0.fq.gz")
            .getCompression());
    assertEquals(CompressionType.GZIP,
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq.gz")
            .getCompression());
    assertEquals(
        CompressionType.GZIP,
        FileNaming.parse(
            "genericindexgenerator_output_bowtieindex_genome.zip.gz")
            .getCompression());
  }

  @Test
  public void testSetStepId() {

    FileNaming f =
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq");
    assertEquals("filterreads", f.getStepId());
    f.setStepId("blabla");
    assertEquals("blabla", f.getStepId());

    try {
      f.setStepId(" blabla ");
      assertTrue(false);
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      f.setStepId(null);
      assertTrue(false);
    } catch (NullPointerException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testSetPortName() {

    FileNaming f =
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq");
    assertEquals("output", f.getPortName());
    f.setPortName("blabla");
    assertEquals("blabla", f.getPortName());

    try {
      f.setPortName(" blabla ");
      assertTrue(false);
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      f.setPortName(null);
      assertTrue(false);
    } catch (NullPointerException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testSetDataName() {

    FileNaming f =
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq");
    assertEquals("s2", f.getDataName());
    f.setDataName("blabla");
    assertEquals("blabla", f.getDataName());

    try {
      f.setDataName(" blabla ");
      assertTrue(false);
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    try {
      f.setDataName(null);
      assertTrue(false);
    } catch (NullPointerException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testSetFormat() {

    FileNaming f =
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq");
    assertEquals(DataFormats.READS_FASTQ, f.getFormat());
    f.setFormat(DataFormats.READS_TFQ);
    assertEquals(DataFormats.READS_TFQ, f.getFormat());

    try {
      f.setFormat(null);
      assertTrue(false);
    } catch (NullPointerException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testSetFileIndex() {

    FileNaming f =
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq");
    assertEquals(0, f.getFileIndex());
    f.setFileIndex(1);
    assertEquals(1, f.getFileIndex());
    f.setFileIndex(-1);
    assertEquals(-1, f.getFileIndex());
    f.setFileIndex(-2);
    assertEquals(-1, f.getFileIndex());
  }

  @Test
  public void testSetPart() {

    FileNaming f =
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq");
    assertEquals(1, f.getPart());
    f.setPart(0);
    assertEquals(0, f.getPart());
    f.setPart(-1);
    assertEquals(-1, f.getPart());
    f.setPart(-2);
    assertEquals(-1, f.getPart());
  }

  @Test
  public void testSetCompression() {

    FileNaming f =
        FileNaming.parse("filterreads_output_reads_s2_file0_part1.fq");
    assertEquals(CompressionType.NONE, f.getCompression());
    f.setCompression(CompressionType.GZIP);
    assertEquals(CompressionType.GZIP, f.getCompression());

    try {
      f.setCompression(null);
      assertTrue(false);
    } catch (NullPointerException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testFilePrefixStringStringDataFormat() {

    assertEquals("filterreads_output_reads_",
        FileNaming.filePrefix("filterreads", "output", DataFormats.READS_FASTQ));
  }

  @Test
  public void testFilePrefixStringStringString() {

    assertEquals("filterreads_output_reads_",
        FileNaming.filePrefix("filterreads", "output", "reads"));
  }

  @Test
  public void testFileMiddleStringIntInt() {

    assertEquals("s1", FileNaming.fileMiddle("s1", -1, -1));
    assertEquals("s1_file0", FileNaming.fileMiddle("s1", 0, -1));
    assertEquals("s1_part1", FileNaming.fileMiddle("s1", -1, 1));
    assertEquals("s1_file1_part2", FileNaming.fileMiddle("s1", 1, 2));
  }

  @Test
  public void testFileSuffixDataFormatCompressionType() {

    assertEquals(".fq",
        FileNaming.fileSuffix(DataFormats.READS_FASTQ, CompressionType.NONE));
    assertEquals(".fq.bz2",
        FileNaming.fileSuffix(DataFormats.READS_FASTQ, CompressionType.BZIP2));

    try {
      FileNaming.fileSuffix(".fq", null);
      assertTrue(false);
    } catch (NullPointerException e) {
      assertTrue(true);
    }

    try {
      FileNaming.fileSuffix(null, "");
      assertTrue(false);
    } catch (NullPointerException e) {
      assertTrue(true);
    }

  }

  @Test
  public void testFileSuffixStringString() {

    assertEquals(".fq", FileNaming.fileSuffix(".fq", ""));
    assertEquals(".fq.bz2", FileNaming.fileSuffix(".fq", ".bz2"));

    try {
      FileNaming.fileSuffix(".fq", null);
      assertTrue(false);
    } catch (NullPointerException e) {
      assertTrue(true);
    }

    try {
      FileNaming.fileSuffix(null, "");
      assertTrue(false);
    } catch (NullPointerException e) {
      assertTrue(true);
    }
  }

  @Test
  public void testParseFile() {

    assertTrue(FileNaming
        .parse(new File("filterreads_output_reads_s1_file0.fq")).filename()
        .equals("filterreads_output_reads_s1_file0.fq"));
  }

  @Test
  public void testParseDataFile() {

    assertTrue(FileNaming
        .parse(new DataFile("filterreads_output_reads_s1_file0.fq")).filename()
        .equals("filterreads_output_reads_s1_file0.fq"));
  }

  @Test
  public void testParseString() {

    assertTrue(FileNaming.parse("filterreads_output_reads_s1_file0.fq")
        .filename().equals("filterreads_output_reads_s1_file0.fq"));

    assertTrue(FileNaming.parse("filterreads_output_reads_s1_file0.fq.bz2")
        .filename().equals("filterreads_output_reads_s1_file0.fq.bz2"));

    assertTrue(FileNaming
        .parse("filterreads_output_reads_s1_file0_part1.fq.bz2").filename()
        .equals("filterreads_output_reads_s1_file0_part1.fq.bz2"));

    assertTrue(FileNaming
        .parse("genericindexgenerator_output_bowtieindex_genome.zip")
        .filename()
        .equals("genericindexgenerator_output_bowtieindex_genome.zip"));

    assertTrue(FileNaming
        .parse("genericindexgenerator_output_bowtieindex_genome_part5.zip")
        .filename()
        .equals("genericindexgenerator_output_bowtieindex_genome_part5.zip"));

    assertTrue(FileNaming
        .parse("genericindexgenerator_output_bowtieindex_genome.zip.gz")
        .filename()
        .equals("genericindexgenerator_output_bowtieindex_genome.zip.gz"));

    assertTrue(FileNaming
        .parse("genericindexgenerator_output_bowtieindex_genome_part5.zip.gz")
        .filename()
        .equals("genericindexgenerator_output_bowtieindex_genome_part5.zip.gz"));

  }

  @Test
  public void testIsStepIdValid() {

    assertTrue(FileNaming.isStepIdValid("data01"));
    assertTrue(FileNaming.isStepIdValid("data"));
    assertTrue(FileNaming.isStepIdValid("01"));
    assertTrue(FileNaming.isStepIdValid("0"));
    assertTrue(FileNaming.isStepIdValid("d"));

    assertFalse(FileNaming.isStepIdValid(null));
    assertFalse(FileNaming.isStepIdValid(""));
    assertFalse(FileNaming.isStepIdValid(" "));
    assertFalse(FileNaming.isStepIdValid("data01 "));
    assertFalse(FileNaming.isStepIdValid(" data01"));
    assertFalse(FileNaming.isStepIdValid("data01!"));
    assertFalse(FileNaming.isStepIdValid("data01/"));
    assertFalse(FileNaming.isStepIdValid("data-01"));
    assertFalse(FileNaming.isStepIdValid("data_01"));
  }

  @Test
  public void testIsFormatPrefixValid() {

    assertTrue(FileNaming.isFormatPrefixValid("data01"));
    assertTrue(FileNaming.isFormatPrefixValid("data"));
    assertTrue(FileNaming.isFormatPrefixValid("01"));
    assertTrue(FileNaming.isFormatPrefixValid("0"));
    assertTrue(FileNaming.isFormatPrefixValid("d"));

    assertFalse(FileNaming.isFormatPrefixValid(null));
    assertFalse(FileNaming.isFormatPrefixValid(""));
    assertFalse(FileNaming.isFormatPrefixValid(" "));
    assertFalse(FileNaming.isFormatPrefixValid("data01 "));
    assertFalse(FileNaming.isFormatPrefixValid(" data01"));
    assertFalse(FileNaming.isFormatPrefixValid("data01!"));
    assertFalse(FileNaming.isFormatPrefixValid("data01/"));
    assertFalse(FileNaming.isFormatPrefixValid("data-01"));
    assertFalse(FileNaming.isFormatPrefixValid("data_01"));
  }

  @Test
  public void testIsPortNameValid() {

    assertTrue(FileNaming.isPortNameValid("data01"));
    assertTrue(FileNaming.isPortNameValid("data"));
    assertTrue(FileNaming.isPortNameValid("01"));
    assertTrue(FileNaming.isPortNameValid("0"));
    assertTrue(FileNaming.isPortNameValid("d"));

    assertFalse(FileNaming.isPortNameValid(null));
    assertFalse(FileNaming.isPortNameValid(""));
    assertFalse(FileNaming.isPortNameValid(" "));
    assertFalse(FileNaming.isPortNameValid("data01 "));
    assertFalse(FileNaming.isPortNameValid(" data01"));
    assertFalse(FileNaming.isPortNameValid("data01!"));
    assertFalse(FileNaming.isPortNameValid("data01/"));
    assertFalse(FileNaming.isPortNameValid("data-01"));
    assertFalse(FileNaming.isPortNameValid("data_01"));
  }

  @Test
  public void testIsDataNameValid() {

    assertTrue(FileNaming.isDataNameValid("data01"));
    assertTrue(FileNaming.isDataNameValid("data"));
    assertTrue(FileNaming.isDataNameValid("01"));
    assertTrue(FileNaming.isDataNameValid("0"));
    assertTrue(FileNaming.isDataNameValid("d"));

    assertFalse(FileNaming.isDataNameValid(null));
    assertFalse(FileNaming.isDataNameValid(""));
    assertFalse(FileNaming.isDataNameValid(" "));
    assertFalse(FileNaming.isDataNameValid("data01 "));
    assertFalse(FileNaming.isDataNameValid(" data01"));
    assertFalse(FileNaming.isDataNameValid("data01!"));
    assertFalse(FileNaming.isDataNameValid("data01/"));
    assertFalse(FileNaming.isDataNameValid("data-01"));
    assertFalse(FileNaming.isDataNameValid("data_01"));
  }

  @Test
  public void testIsFilenameValidDataFile() {

    assertTrue(FileNaming.isFilenameValid(new DataFile(
        "filterreads_output_reads_s1_file0.fq")));
    assertFalse(FileNaming.isFilenameValid(new DataFile("toto.txt")));
  }

  @Test
  public void testIsFilenameValidFile() {

    assertTrue(FileNaming.isFilenameValid(new File(
        "filterreads_output_reads_s1_file0.fq")));
    assertFalse(FileNaming.isFilenameValid(new File("toto.txt")));
  }

  @Test
  public void testIsFilenameValidString() {

    assertTrue(FileNaming
        .isFilenameValid("filterreads_output_reads_s1_file0.fq"));
    assertTrue(FileNaming
        .isFilenameValid("filterreads_output_reads_s1_file0_part1.fq"));
    assertTrue(FileNaming
        .isFilenameValid("genericindexgenerator_output_bowtieindex_genome.zip"));
    assertTrue(FileNaming
        .isFilenameValid("genomedescgenerator_output_genomedesc_genome.txt"));
    assertTrue(FileNaming.isFilenameValid("mapreads_output_alignments_s1.sam"));
    assertTrue(FileNaming
        .isFilenameValid("expression_output_expression_s1.tsv"));

    assertFalse(FileNaming.isFilenameValid("toto.txt"));
    assertFalse(FileNaming.isFilenameValid("filterreads_output_reads_s1.fq"));

  }

}
