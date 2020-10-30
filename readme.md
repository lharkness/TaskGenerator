### Overview
TaskGenerator is used to define sequences and rules for use in experiments.  Once these sequences and rules have been
defined TaskGenerator can validate these rules and generate artifacts for use in running the experiment.

### Getting Started
Download the jar file.

### Usage
Ensure you have Java installed.  Create a setup file (refer to the `setup.txt` file in the project)
Create a test data file (refer to the `testData.txt` file in the project).  Execute the following command:

`java -jar \path\to\setup.txt \path\to\testData.txt`

The results of the execution will be in two files.  `task.js` is the artifact that
implements the task.  `validationResults.txt` are the results of running the validation.

### Notes
This is mostly vaporware at the moment.  Milestone 1 produces a working generator/validator.  
Milestone 2 produces Task artifacts.

![overview](exerciseCreation.png)  
 