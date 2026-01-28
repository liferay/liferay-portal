# Liferay Headless Content Page - Next.js Sample

This is a [Next.js](https://nextjs.org) made to consume [Liferay](https://www.liferay.com/)'s CMS Content headless APIs.

## Prerequisites

Before starting, ensure you have installed:

- Git
- Node.js 22+
- Liferay Portal 2025.Q4+

## Getting Started

### 1. Clone the template

To clone the `content-page` template, run:

```bash
curl -sL https://raw.githubusercontent.com/liferay/liferay-portal/master/modules/integrations/vercel/clone-template.sh | bash -s -- content-page
```

And then go to your newly created repository:

```bash
cd content-page
```

### 2. Setup your local Liferay instance

Currently, to run a Liferay DXP with the CMS site enabled, we need to enable the following feature flags:

- Release FF:
    - LPD-32050 (Enhancements to Object Entry Localization)
    - LPD-34594 (Root Object Definitions)
- Beta FF:
    - LPD-17564 (CMS)

1. Go to your running liferay instance [http://localhost:8080/](http://localhost:8080/);

1. Login with email and password;

![Image](../images/image-1.png)

Liferay provides a few predefined content structures, but you're free to create your own. Let's create an `Event` structure:

1. Go to the **Structures** page and add a new `Content` structure;
   <br />![create structure 01](./images/create-structure-01.png)

1. Edit the structure name and make it available for all spaces:
   <br />![create structure 02](./images/create-structure-02.png)

1. Edit the `Title` field:
   <br />![create structure 03](./images/create-structure-03.png)

1. Edit the `Content` field:
   <br />![create structure 04](./images/create-structure-04.png)

1. Edit the `Summary` field:
   <br />![create structure 05](./images/create-structure-05.png)

1. Edit the `Image` field:
   <br />![create structure 06](./images/create-structure-06.png)

1. Edit the `Location Map Url` field:
   <br />![create structure 07](./images/create-structure-07.png)

1. Edit the `Location Name` field:
    <br />![create structure 08](./images/create-structure-08.png)

1. Edit the `Registration Link` field:
    <br />![create structure 09](./images/create-structure-09.png)

1. Publish it.

Now you're able to create new `Event`s:

![create structure 10](./images/create-structure-10.png)

Save the ID present in the URL and Global Menu:

![create structure 11](./images/create-structure-11.png)

Fill the form and save your content.

Due to security reasons, Liferay doesn't publicly expose some APIs, and that's why we need to add a [Service Access Policy](https://learn.liferay.com/w/dxp/security-and-administration/security/securing-web-services/setting-service-access-policies). To do that:

1. Go to the [Default Service Access Policies](https://learn.liferay.com/w/dxp/security-and-administration/security/securing-web-services/setting-service-access-policies) page;

1. Open the existing `OBJECT_DEFAULT` rule;

1. Add a new row and fill it with the following:
    - **Service Class:** `com.liferay.object.rest.internal.resource.v1_0.ObjectEntryRelatedObjectsResourceImpl`
    - **Method Name:** `getCurrentObjectEntriesObjectRelationshipNamePage`

Once that API is now public, we need to make a Guest user (an unauthenticated one) able to see the content provided by it. To do that, follow the steps in [Defining Role Permissions](https://learn.liferay.com/w/dxp/security-and-administration/users-and-permissions/roles-and-permissions/defining-role-permissions) and add the following permissions for the `Guest` role:

- Under `Objects > [ YOUR CUSTOM CONTENT ENTITY ] > [ YOUR CUSTOM CONTENT ENTITY ]`, add `VIEW` permission.
    <br />![guest permissions](./images/guest-permissions.png)

### 3. Run your template

To get your template up and running, first, install the dependencies:

```bash
npm install
```

And before starting it, define your environment variables.

1. Copy the `.env.example` file to `.env`

1. Define:
    - `LIFERAY_LANGUAGES`: The available languages that you can consume to extract data to display (e.g.: `en_US,es_ES,pt_BR`).
    - `LIFERAY_CONTENT_PATH`: Your content path, including ID
        - If you're using a custom structure, it will be like `/o/c/[ structure_name ]/[ content_ID ]`;
            - In our example, that would be: `/o/c/events/35367`
        - If you're using the existing Basic Web Content, it will be like `/o/cms/basic-documents/[ content_ID ]`.
        - You can find the `content_ID` in the content details page.

Once you're done, run the development server:

```bash
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

You can start editing the page by modifying `app/page.tsx`. The page auto-updates as you edit the file.

## Learn More

To learn more about Liferay's headless APIs, take a look at the following resources:

- [Foundations of Liferay Headless APIs](https://learn.liferay.com/l/29393515)
- [Mastering Consuming Liferay Headless APIs](https://learn.liferay.com/l/29852017)
- [Learn Next.js](https://nextjs.org/learn)