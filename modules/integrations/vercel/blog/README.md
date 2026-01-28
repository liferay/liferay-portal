# Liferay Headless Blog - Next.js Sample

This is a [Next.js](https://nextjs.org) made to consume [Liferay](https://www.liferay.com/)'s CMS Blog headless APIs.

## Prerequisites

Before starting, ensure you have installed:

- Git
- Node.js 22+
- Liferay Portal 2025.Q4+

## Getting Started

### 1. Clone the template

To clone the `blog` template, run:

```bash
curl -sL https://raw.githubusercontent.com/liferay/liferay-portal/master/modules/integrations/vercel/clone-template.sh | bash -s -- blog
```

And then go to your newly created repository:

```bash
cd blog
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

Currently, we need to create a CMS site. Follow the image, click on **Get Started** and follow the installation process until the end.

Due to security reasons, Liferay doesn't publicly expose some APIs, and that's why we need to add a [Service Access Policy](https://learn.liferay.com/w/dxp/security-and-administration/security/securing-web-services/setting-service-access-policies). To do that:

1. Go to the [Default Service Access Policies](https://learn.liferay.com/w/dxp/security-and-administration/security/securing-web-services/setting-service-access-policies) page;

1. Open the existing `OBJECT_DEFAULT` rule;

1. Add a new row and fill it with the following:
    - **Service Class:** `com.liferay.object.rest.internal.resource.v1_0.ObjectEntryResourceImpl`
    - **Method Name:** `getScopeScopeKeyPage`

Once that API is now public, we need to make a Guest user (an unauthenticated one) able to see the content provided by it. To do that, follow the steps in [Defining Role Permissions](https://learn.liferay.com/w/dxp/security-and-administration/users-and-permissions/roles-and-permissions/defining-role-permissions) and add the following permissions for the `Guest` role:

- Under `Objects > Blog > Blog`, add `VIEW` permission.

### 3. Run your template

To get your template up and running, first, install the dependencies:

```bash
npm install
```

And before starting it, define your environment variables.

1. Copy the `.env.example` file to `.env`

1. Define:

    - `LIFERAY_HOST`: Your Liferay instance URL (`http://localhost:8080` for local development).
    - `LIFERAY_SPACE_ID`: Your CMS Space ID (aka Group ID, or Scope ID), you can get it in the Space settings.

1. Feel free to add Blog content for your local testing:

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