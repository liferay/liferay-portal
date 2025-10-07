# Talend Job Designs for Liferay

This project contains a Talend Open Studio workspace for working with Liferay Talend job designs.

## Prerequisites

* Apache Maven 3.3+
* JDK 1.8+
* Talend Open Studio 7.1.1 for Data Integration or for ESB
	* Components API v0.25.3

Talend Open Studio is available at these locations:
* [Talend Open Studio home](https://www.talend.com/products/talend-open-studio/)
* [7.1.1 Direct link](https://download-mirror2.talend.com/esb/release/V7.1.1/TOS_ESB-20181026_1147-V7.1.1.zip)

> Notes for all Talend releases are here: <https://www.talend.com/products/data-integration-manuals-release-notes/>

## Importing Liferay Talend Job Design Sources Into a Workspace

1. Start Talend Open Studio.

1. Select the option to import a project.

1. In the import form, name the project and choose to select the project root directory.

1. Click the browse button, navigate to your `[repository home]/modules/integrations/talend/talend-job-designs/tos-711-liferay` folder, and select it.

1. Click the import button.

Talend Open Studio imports the Liferay Talend job design sources into your workspace.

## Contributing to Job Designs

Here's how to contribute to Liferay Talend job designs:

1. In Talend Open Studio, apply changes to a job design.

1. Test the job design.

1. Copy all modified `*.item` files back to Liferay repository and commit them.

1. Push your changes to your fork of <https://github.com/liferay/liferay-portal>.

1. In GitHub, send a pull request to the `liferay-commerce` GitHub user's `master` branch.

1. In [JIRA](https://issues.liferay.com/projects/COMMERCE/issues), notify the Data Intagration team about your contribution by creating a *Liferay Commerce (COMMERCE)* project ticket.