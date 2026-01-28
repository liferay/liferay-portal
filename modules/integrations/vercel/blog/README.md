# Liferay Headless Blog - Next.js Sample
<!-- In LiferayStyle we remove needless words and fillers ... maybe:  "This [Next.js](...) application consumes [Liferay](...) CMS Blog APIs"-->
This is a [Next.js](https://nextjs.org) made to consume [Liferay](https://www.liferay.com/)'s CMS Blog headless APIs.

 ## Prerequisites<!--I wouldn't use it as a section. Maybe just "Prerequisites:"-->

<!-- Unecessary, you can go ahead and list the prerequisites since the section title already desclibes it.

Before starting, ensure you have installed: -->

- Git
- Node.js 22+
- Liferay Portal 2025.Q4+

## Getting Started

### 1. Clone the template
<!-- lists are very important, so maybe instead of "to clone..." go straight to :
1. Run ``` bash ....```
1. Access the created repository ``` bash``` -->
To clone the `blog` template, run:

```bash
curl -sL https://raw.githubusercontent.com/liferay/liferay-portal/master/modules/integrations/vercel/clone-template.sh | bash -s -- blog
```

And then go to your newly created repository:

```bash
cd blog
```

### 2. Setup your local Liferay instance

<!-- Use a note to explain FFs: 
!!! important
    Currently, this feature is behind a beta feature flag (LPD-XXXXX) and also depends on release feature flags (LPS-XXXXXX and LPD-XXXXX). Read [Feature Flags](../security-and-administration/administration/configuring-liferay/feature-flags.md) for more information. -->
Currently, to run a Liferay DXP with the CMS site enabled, we need to enable the following feature flags:

- Release FF:
    - LPD-32050 (Enhancements to Object Entry Localization)
    - LPD-34594 (Root Object Definitions)
- Beta FF:
    - LPD-17564 (CMS)

<!-- For steps, instead of using semicolons(;), use full stops(.) -->
<!-- Remove "running" and capsize Liferay: Go to your Liferay instance. -->
1. Go to your running liferay instance [http://localhost:8080/](http://localhost:8080/);

<!-- add "your" - Login with your email and password. -->
1. Login with email and password;

<!-- In documentation, we have to make sure your alt text is a complete sentence. Describe what the image is rather than naming it randomly. -->
![Image](../images/image-1.png)

<!-- Usage of bold text is very rare, try to use italics when you direct people to click on something. Also the image is not available to me. -->
Currently, we need to create a CMS site. Follow the image, click on **Get Started** and follow the installation process until the end.

<!-- ## Add the Service Access Policy -->

Due to security reasons, Liferay doesn't publicly expose some APIs, and that's why we need to add the [Service Access Policy](https://learn.liferay.com/w/dxp/security-and-administration/security/securing-web-services/setting-service-access-policies). To do that:
<!-- With the addiction of a title section we can remove "To do that:" -->

1. Go to the [Default Service Access Policies](https://learn.liferay.com/w/dxp/security-and-administration/security/securing-web-services/setting-service-access-policies) page;

1. Open the existing `OBJECT_DEFAULT` rule;

1. Add a new row and fill it with the following:
    - **Service Class:** `com.liferay.object.rest.internal.resource.v1_0.ObjectEntryResourceImpl`
    - **Method Name:** `getScopeScopeKeyPage`

 <!-- 1. Go to the [Default Service Access Policies](https://learn.liferay.com/w/dxp/security-and-administration/security/securing-web-services/setting-service-access-policies) page.

1. Open the `OBJECT_DEFAULT` rule.

1. Add a new row and fill it with:
    - Service Class: `com.liferay.object.rest.internal.resource.v1_0.ObjectEntryResourceImpl`
    - Method Name: `getScopeScopeKeyPage` -->

Once that API is now public, we need to make a Guest user (an unauthenticated one) able to see the content provided by it. To do that, follow the steps in [Defining Role Permissions](https://learn.liferay.com/w/dxp/security-and-administration/users-and-permissions/roles-and-permissions/defining-role-permissions) and add the following permissions for the `Guest` role:

<!-- Once the API is public, make a "Guest user" (unauthenticated) able to see the content provided by it. Follow the steps of [Defining Role Permissions](https://learn.liferay.com/w/dxp/security-and-administration/users-and-permissions/roles-and-permissions/defining-role-permissions) and add the permissions: -->

- Under `Objects > Blog > Blog`, add `VIEW` permission.

### 3. Run your template

<!-- Unecessary. To get your template up and running, first, install the dependencies: -->

<!-- 1. Install the dependencies, run ... -->
```bash
npm install
```
<!-- 1. Define your environment variables:  -->
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

<!-- Make a step by step with all pieces of information, don't be afraid to use imperative form.-->

## Learn More
<!-- In documentation, we use ## Related Topics -->

<!-- Unecessary. To learn more about Liferay's headless APIs, take a look at the following resources: -->

- [Foundations of Liferay Headless APIs](https://learn.liferay.com/l/29393515)
- [Mastering Consuming Liferay Headless APIs](https://learn.liferay.com/l/29852017)
- [Learn Next.js](https://nextjs.org/learn)