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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import fr.ens.transcriptome.eoulsan.AbstractEoulsanRuntime;
import fr.ens.transcriptome.eoulsan.EoulsanRuntime;
import fr.ens.transcriptome.eoulsan.EoulsanRuntimeException;
import fr.ens.transcriptome.eoulsan.Settings;
import fr.ens.transcriptome.eoulsan.core.InputPort;
import fr.ens.transcriptome.eoulsan.core.OutputPort;
import fr.ens.transcriptome.eoulsan.core.StepContext;
import fr.ens.transcriptome.eoulsan.data.Data;
import fr.ens.transcriptome.eoulsan.data.DataFile;
import fr.ens.transcriptome.eoulsan.data.DataFormat;

/**
 * This class define a step context.
 * @author Laurent Jourdren
 * @since 2.0
 */
public class WorkflowStepContext implements StepContext, Serializable {

  /** Serialization version UID. */
  private static final long serialVersionUID = 8288158811122533646L;

  private static int instanceCounter;

  private final int id;
  private final WorkflowContext workflowContext;
  private String contextName;
  private final AbstractWorkflowStep step;

  private final Map<String, Data> inputData = Maps.newHashMap();
  private final Map<String, AbstractData> outputData = Maps.newHashMap();

  //
  // Getters
  //

  public int getId() {
    return this.id;
  }

  @Override
  public String getContextName() {

    return this.contextName;
  }

  @Override
  public String getLocalWorkingPathname() {

    return this.workflowContext.getLocalWorkingPathname();
  }

  @Override
  public String getHadoopWorkingPathname() {

    return this.workflowContext.getHadoopWorkingPathname();
  }

  @Override
  public String getLogPathname() {

    return this.workflowContext.getLogPathname();
  }

  @Override
  public String getOutputPathname() {
    return this.workflowContext.getOutputPathname();
  }

  @Override
  public String getStepWorkingPathname() {

    return this.step.getStepWorkingDir().getSource();
  }

  @Override
  public String getJobId() {
    return this.workflowContext.getJobId();
  }

  @Override
  public String getJobHost() {
    return this.workflowContext.getJobHost();
  }

  @Override
  public long getContextCreationTime() {
    return this.workflowContext.getContextCreationTime();
  }

  @Override
  public String getDesignPathname() {
    return this.workflowContext.getDesignPathname();
  }

  @Override
  public String getWorkflowPathname() {
    return this.workflowContext.getWorkflowPathname();
  }

  @Override
  public String getJarPathname() {
    return this.workflowContext.getJarPathname();
  }

  @Override
  public String getJobUUID() {
    return this.workflowContext.getJobUUID();
  }

  @Override
  public String getJobDescription() {
    return this.workflowContext.getJobDescription();
  }

  @Override
  public String getJobEnvironment() {
    return this.workflowContext.getJobEnvironment();
  }

  @Override
  public String getCommandName() {
    return this.workflowContext.getCommandName();
  }

  @Override
  public String getCommandDescription() {
    return this.workflowContext.getCommandDescription();
  }

  @Override
  public String getCommandAuthor() {
    return this.workflowContext.getCommandAuthor();
  }

  @Override
  public Workflow getWorkflow() {

    return this.workflowContext.getWorkflow();
  }

  @Override
  public WorkflowStep getCurrentStep() {
    return this.step;
  }

  /**
   * Get the AbstractWorkflowStep object.
   * @return a AbstractWorkflowStep object
   */
  AbstractWorkflowStep getStep() {

    return this.step;
  }

  //
  // Setters
  //

  @Override
  public void setContextName(final String contextName) {

    Preconditions.checkNotNull(contextName,
        "contextName argument cannot be null");

    // TODO Check if the context name is unique for the step

    this.contextName = contextName.trim();
  }

  //
  // Other methods
  //

  @Override
  public AbstractEoulsanRuntime getRuntime() {

    return this.workflowContext.getRuntime();
  }

  @Override
  public Settings getSettings() {

    return this.workflowContext.getSettings();
  }

  @Override
  public Logger getLogger() {

    return this.workflowContext.getLogger();
  }

  @Override
  public Data getInputData(final String portName) {

    Preconditions.checkNotNull(portName, "portName cannot be null");

    return new UnmodifiableData(this.inputData.get(portName));
  }

  @Override
  public Data getInputData(final DataFormat format) {

    return getInputData(getInputPortNameForFormat(format));
  }

  @Override
  public Data getOutputData(final String portName, final String dataName) {

    Preconditions.checkNotNull(portName, "portName cannot be null");

    final AbstractData data = this.outputData.get(portName);
    data.setName(dataName);

    return data;
  }

  @Override
  public Data getOutputData(final String portName, final Data origin) {

    Preconditions.checkNotNull(origin, "origin cannot be null");

    return getOutputData(portName, origin.getName());
  }

  @Override
  public Data getOutputData(final DataFormat format, final String dataName) {

    return getOutputData(getOutputPortNameForFormat(format), dataName);
  }

  @Override
  public Data getOutputData(final DataFormat format, final Data origin) {

    Preconditions.checkNotNull(origin, "origin cannot be null");

    return getOutputData(getOutputPortNameForFormat(format), origin.getName());
  }

  //
  // Package methods
  //

  /**
   * Get raw access to input data stored in the object.
   * @param port name of the input port
   * @return a Data object
   */
  public Data getInputData(final InputPort port) {

    Preconditions.checkNotNull(port, "port cannot be null");

    if (!this.inputData.containsKey(port.getName()))
      throw new EoulsanRuntimeException("Unknown port: "
          + port.getName() + " for step " + this.step.getId());

    return this.inputData.get(port.getName());
  }

  /**
   * Get raw access to output data stored in the object.
   * @param port name of the output port
   * @return a Data object
   */
  Data getOutputData(final OutputPort port) {

    Preconditions.checkNotNull(port, "port cannot be null");

    if (!this.outputData.containsKey(port.getName()))
      throw new EoulsanRuntimeException("Unknown port: "
          + port.getName() + " for step " + this.step.getId());

    return this.outputData.get(port.getName());
  }

  AbstractWorkflowStep getWorkflowStep() {

    return this.step;
  }

  //
  // Private methods
  //

  private String getInputPortNameForFormat(final DataFormat format) {

    checkNotNull(format, "The format is null");

    final List<WorkflowInputPort> ports =
        this.step.getWorkflowInputPorts().getPortsWithDataFormat(format);

    switch (ports.size()) {

    case 0:
      throw new EoulsanRuntimeException("The step "
          + this.step.getId() + " do not provide an input port with format: "
          + format);
    case 1:
      return ports.get(0).getName();
    default:
      throw new EoulsanRuntimeException("The step "
          + this.step.getId()
          + " provide more than one input port with format: " + format);
    }
  }

  private String getOutputPortNameForFormat(final DataFormat format) {

    checkNotNull(format, "The format is null");

    final List<WorkflowOutputPort> ports =
        this.step.getWorkflowOutputPorts().getPortsWithDataFormat(format);

    switch (ports.size()) {

    case 0:
      throw new EoulsanRuntimeException("The step "
          + this.step.getId() + " do not provide an output port with format: "
          + format);
    case 1:
      return ports.get(0).getName();
    default:
      throw new EoulsanRuntimeException("The step "
          + this.step.getId()
          + " provide more than one output port with format: " + format);
    }
  }

  //
  // Other methods
  //

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("id", this.id)
        .add("step", this.step.getId()).add("contextName", this.contextName)
        .toString();
  }

  //
  // Serialization methods
  //

  /**
   * Serialize the WorkflowStepContext object.
   * @param file output DataFile
   * @throws IOException if an error occurs while creating the file
   */
  public void serialize(final File file) throws IOException {

    checkNotNull(file, "file argument cannot be null");

    serialize(new FileOutputStream(file));
  }

  /**
   * Serialize the WorkflowStepContext object.
   * @param file output DataFile
   * @throws IOException if an error occurs while creating the file
   */
  public void serialize(final DataFile file) throws IOException {

    checkNotNull(file, "file argument cannot be null");

    serialize(file.create());
  }

  /**
   * Serialize the WorkflowStepContext object.
   * @param out output stream
   * @throws IOException if an error occurs while creating the file
   */
  public void serialize(final OutputStream out) throws IOException {

    checkNotNull(out, "out argument cannot be null");

    final ObjectOutputStream oos = new ObjectOutputStream(out);

    oos.writeObject(this);
    oos.writeObject(EoulsanRuntime.getSettings());
    oos.close();
  }

  /**
   * Deserialize the WorkflowStepContext object. Warning: this method update the
   * values of the settings of the Eoulsan runtime.
   * @param file input DataFile
   * @throws IOException if an error occurs while reading the file
   */
  public static WorkflowStepContext deserialize(final File file)
      throws IOException {

    checkNotNull(file, "file argument cannot be null");

    return deserialize(new FileInputStream(file));
  }

  /**
   * Deserialize the WorkflowStepContext object. Warning: this method update the
   * values of the settings of the Eoulsan runtime.
   * @param file input DataFile
   * @throws IOException if an error occurs while reading the file
   */
  public static WorkflowStepContext deserialize(final DataFile file)
      throws IOException {

    checkNotNull(file, "file argument cannot be null");

    return deserialize(file.open());
  }

  /**
   * Deserialize the WorkflowStepContext object. Warning: this method update the
   * values of the settings of the Eoulsan runtime.
   * @param in input stream
   * @throws IOException if an error occurs while reading the file
   */
  public static WorkflowStepContext deserialize(final InputStream in)
      throws IOException {

    checkNotNull(in, "in argument cannot be null");

    try {
      final ObjectInputStream ois = new ObjectInputStream(in);

      // Read WorkflowStepContext object
      final WorkflowStepContext result = (WorkflowStepContext) ois.readObject();

      // Read Settings object
      final Settings settings = (Settings) ois.readObject();

      // Overwrite current Settings of Eoulsan runtime
      EoulsanRuntime.getSettings().setSettings(settings);

      ois.close();

      return result;

    } catch (ClassNotFoundException e) {
      throw new EoulsanRuntimeException(e.getMessage());
    }
  }

  //
  // Constructor
  //

  /**
   * Constructor.
   * @param step step related to the context
   */
  WorkflowStepContext(final WorkflowContext workflowContext,
      final AbstractWorkflowStep step, Map<InputPort, Data> inputData,
      Map<OutputPort, AbstractData> outputData) {

    checkNotNull(workflowContext, "workflow context cannot be null");
    checkNotNull(step, "step cannot be null");

    synchronized (this.getClass()) {
      this.id = (++instanceCounter);
    }
    this.contextName = "context" + id;

    this.workflowContext = workflowContext;
    this.step = step;

    // Copy input and output data
    for (Map.Entry<InputPort, Data> e : inputData.entrySet()) {
      this.inputData.put(e.getKey().getName(), e.getValue());
    }

    for (Map.Entry<OutputPort, AbstractData> e : outputData.entrySet()) {
      this.outputData.put(e.getKey().getName(), e.getValue());
    }
  }

}
