{
	"dependencies": {
	},
	"devDependencies": {
		"@types/node": "^12",
		"typescript": "^4.0 || ^5.0"
	},
	"exports": {
		".": "./src/index.ts"<#list escapedVersions?sort as escapedVersion>,
		"./${escapedVersion}": "./src/${escapedVersion}/index.ts"</#list>
	},
	"main": "src/index.ts",
	"name": "@liferay/${clientName}",
	"private": true,
	"type": "commonjs",
	"typesVersions": {
		"*": {
			<#list escapedVersions?sort as escapedVersion>
			"${escapedVersion}": [
				"src/${escapedVersion}/index.ts"
			]<#sep>,</#sep>
			</#list>
		}
	},
	"version": "1.0.0"
}