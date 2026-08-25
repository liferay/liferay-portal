# Using liferay-learn:message Tags

You can provide direct links to [Liferay Learn](https://learn.liferay.com) documentation from Liferay's UI with the `liferay-learn:message` tag. For example, the *Click to Chat* app links to the [Chatwoot](https://learn.liferay.com/w/dxp/site-building/personalizing-site-experience/enabling-automated-live-chat-systems/getting-a-chat-provider-account-id/chatwoot) Liferay Learn article.

![The Click to Chat page links to the Chatwoot article.](./images/01.png)

Now users can click your `liferay-learn:message` links to get help!

The links have two parts:

1. A JSON file specifying the linked documentation.

1. A `liferay-learn:message` tag pointing to the JSON file and one of its links.

Keeping the resources separate from your JSP code makes it easier to update link labels and URLs and add translations.

**Note:** This is safe to use: the `liferay-learn:message` tag renders nothing if you accidentally reference a missing JSON file or an unspecified resource entry.

Start with specifying a resource.

## Adding Resources in a JSON File

1. In this folder (`learn-resources`), create a JSON file named after your module.

1. Create an element for each resource on Liferay Learn. For example, the [`learn-resources/data/marketplace-store-web.json`](https://github.com/liferay/liferay-portal/blob/master/learn-resources/data/marketplace-store-web.json) file has these resource entries:

	```json
	{
		"download-app": { // Resource key
			"en_US": {
				"message": "How can I download an app?", // Link label
				"url": "https://learn.liferay.com/dxp/latest/en/system-administration/installing-and-managing-apps/installing-apps/downloading-apps.html" // Resource URL
			}
		},
		"purchase-app": {
			"en_US": {
				"message": "How can I purchase an app?",
				"url": "https://learn.liferay.com/dxp/latest/en/system-administration/installing-and-managing-apps/getting-started/using-marketplace.html"
			}
		}
	}
	```

The example resource entries have the keys `download-app` and `purchase-app`. The keys are unique within the JSON file. You can provide each resource in multiple locales. For example, the resources above are in the `en_US` locale. For each locale, assign the `url` to the resource location and the `message` to a label for the resource link.

**Note:** The only valid locales on Liferay Learn are `en-US` and `ja-JP`.

## Adding `liferay-learn:message` Tags to a JSP

In your module's JSP, link to the resources using `liferay-learn:message` tags. For example, use this code in the `marketplace-store-web` module's `view.jsp` file to reference the `learn-resources/data/marketplace-store-web.json` file's `download-app` resource:

```jsp
<%@ taglib uri="http://liferay.com/tld/learn" prefix="liferay-learn" %>

<liferay-learn:message
    key="download-app"
    resource="marketplace-store-web"
/>
```

The first line above includes the `liferay-learn` tag library. The `liferay-learn:message` tag links to the `download-app` resource in the `learn-resources/data/marketplace-store-web.json` file. When the JSP renders, the text *How can I download an app?* links to the resource located at <https://learn.liferay.com/dxp/latest/en/system-administration/installing-and-managing-apps/installing-apps/downloading-apps.html>.

That's how you link to Liferay Learn resources!

> A CDN server hosts the JSON files. For example, here's how the `<liferay-learn:message key="download-app" resource="marketplace-store-web" />` tag works:
>
> 1. The tag checks for the resource file (JSON file with prefix `marketplace-store-web`) on the *local* CDN server at <https://learn-resources.liferay.com/marketplace-store-web.json>.
> 1. The local server checks the *global* server at <http://s3.amazonaws.com/learn-resources.liferay.com/marketplace-store-web.json> for updates to the resource.
> 1. If the local resource is valid, it's served immediately. Otherwise, the local server serves the resource after refreshing the local resource cache with the latest update from the global server.
>
> Note: The cache refreshes every four hours.

## Previewing Liferay Learn Resource Links

If you want to test your link, you don't have to recompile your module. Point the `learn.resources.dir` portal property at this folder's `data` directory:

```properties
learn.resources.dir=/path/to/liferay-portal/learn-resources/data
```

Resources are then read from that directory on every request, so your edits show up immediately. Without the property, resources are read from <https://s3.amazonaws.com/learn-resources.liferay.com> and cached for four hours.

Set `learn.resources.mode=off` to disable the Learn tag library.

Use these properties with a local bundle. With Docker, use the `LIFERAY_LEARN_PERIOD_RESOURCES_PERIOD_DIR` and `LIFERAY_LEARN_PERIOD_RESOURCES_PERIOD_MODE` environment variables instead.

## Adding a Resource Link to a React Component

To use [the `search-experiences-web.json` file's `advanced-configuration` resource key](https://github.com/liferay/liferay-portal/blob/master/learn-resources/data/search-experiences-web.json#L2-L7),

1. In the JSP, use the `LearnMessageUtil.getReactDataJSONObject` Java method to retrieve the resource data to pass into the React component.

	```html
	<%@ page import="com.liferay.learn.LearnMessageUtil" %>

	<react:component
		module='path/to/component'
		props='<%=
			HashMapBuilder.<String, Object>put(
				"learnResources", LearnMessageUtil.getReactDataJSONObject("search-experiences-web")
			).build()
		%>'
	/>
	```

	To retrieve multiple resources, a string array can be passed into `getReactDataJSONObject`. For example: `LearnMessageUtil.getReactDataJSONObject(new String[] {"portal-search-web", "search-experiences-web"})`

1. In the React component, use `LearnResourcesContext` to provide the resource and the `LearnMessage` component to display the link.

	```javascript
	import {LearnMessage, LearnResourcesContext} from 'frontend-js-components-web';

	<LearnResourcesContext.Provider value={learnResources}>
		<LearnMessage
			resource='search-experiences-web'
			resourceKey='advanced-configuration'
		/>
	</LearnResourcesContext.Provider>
	```

	The `LearnMessage` component renders a `ClayLink` and additional props are passed into it.

## Guidelines

Here are some guidelines for writing the JSON files and tags.

### Name the JSON Files After the Web Modules That Use the Resources

For example, if you want the `foo-web` module's JSPs to link to resources, create the resources in a JSON file called `liferay-resources/foo-web.json`.

### Make Resource Keys Unique Per JSON File

Don't duplicate resource keys in the same JSON file.

### Name Lone Resource Keys `general`

If a JSON file has only one resource key, name the key `general`.