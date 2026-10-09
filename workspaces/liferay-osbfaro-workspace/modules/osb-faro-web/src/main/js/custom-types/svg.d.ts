// An SVG imported by the app is an Analytics Cloud icon, which SVGR bundles as
// a React component. Append `?url` to import a file as a URL instead.

declare module '*.svg' {
	import React from 'react';

	const Component: React.ForwardRefExoticComponent<
		React.SVGProps<SVGSVGElement> & React.RefAttributes<SVGSVGElement>
	>;

	export default Component;
}

declare module '*.svg?url' {
	const url: string;

	export default url;
}
