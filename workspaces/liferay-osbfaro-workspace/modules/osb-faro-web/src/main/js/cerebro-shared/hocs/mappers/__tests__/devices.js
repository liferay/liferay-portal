import {getDevicesMapper} from '../devices';

describe('Shared HOCs Mappers - Devices', () => {
	it('should map devices information', () => {
		const mapper = getDevicesMapper(
			(result) => result.form.submissionsMetric
		);

		const data = {
			data: {
				form: {
					submissionsMetric: {
						browser: [
							{
								value: 2248.0,
								valueKey: 'Chrome',
							},
							{
								value: 782.0,
								valueKey: 'Firefox',
							},
							{
								value: 137.0,
								valueKey: 'Chrome Mobile',
							},
							{
								value: 136.0,
								valueKey: 'Safari',
							},
							{
								value: 124.0,
								valueKey: 'Mobile Safari',
							},
							{
								value: 109.0,
								valueKey: 'Edge',
							},
							{
								value: 39.0,
								valueKey: 'Unknown',
							},
							{
								value: 20.0,
								valueKey: 'Samsung Browser',
							},
							{
								value: 17.0,
								valueKey: 'Opera Desktop',
							},
							{
								value: 10.0,
								valueKey: 'Chromium Project',
							},
							{
								value: 8.0,
								valueKey: 'Android',
							},
							{
								value: 7.0,
								valueKey: 'Unknown iOS App',
							},
							{
								value: 6.0,
								valueKey: 'Vivaldi',
							},
							{
								value: 5.0,
								valueKey: 'Opera Mini for iOS',
							},
							{
								value: 5.0,
								valueKey: 'Unknown Linux App',
							},
							{
								value: 4.0,
								valueKey: 'Opera Mobile',
							},
							{
								value: 3.0,
								valueKey: 'Yowser for Windows',
							},
							{
								value: 2.0,
								valueKey: 'Firefox for Mobile',
							},
							{
								value: 2.0,
								valueKey:
									'Unknown Mac OS X App - WebKit Engine',
							},
							{
								value: 2.0,
								valueKey: 'Waterfox',
							},
							{
								value: 1.0,
								valueKey: 'Chrome for iOS',
							},
							{
								value: 1.0,
								valueKey: 'Edge Mobile',
							},
							{
								value: 1.0,
								valueKey: 'QQ Browser',
							},
							{
								value: 1.0,
								valueKey: 'UBrowser',
							},
							{
								value: 1.0,
								valueKey: 'Yowser for Linux',
							},
						],
						device: [
							{
								metrics: [
									{
										value: 2065.0,
										valueKey: 'Windows',
									},
									{
										value: 995.0,
										valueKey: 'macOS',
									},
									{
										value: 136.0,
										valueKey: 'Linux',
									},
									{
										value: 107.0,
										valueKey: 'Mac OS X',
									},
									{
										value: 53.0,
										valueKey: 'Ubuntu',
									},
									{
										value: 3.0,
										valueKey: 'ChromeOS',
									},
									{
										value: 2.0,
										valueKey: 'Fedora',
									},
									{
										value: 1.0,
										valueKey: 'Unknown',
									},
								],
								value: 3362.0,
								valueKey: 'Desktop',
							},
							{
								metrics: [
									{
										value: 151.0,
										valueKey: 'Android',
									},
									{
										value: 61.0,
										valueKey: 'iOS',
									},
									{
										value: 9.0,
										valueKey: 'Unknown',
									},
									{
										value: 8.0,
										valueKey: 'FreeBSD',
									},
									{
										value: 1.0,
										valueKey: 'Windows Phone',
									},
								],
								value: 230.0,
								valueKey: 'SmartPhone',
							},
							{
								metrics: [
									{
										value: 67.0,
										valueKey: 'iOS',
									},
									{
										value: 12.0,
										valueKey: 'Android',
									},
								],
								value: 79.0,
								valueKey: 'Tablet',
							},
						],
					},
				},
			},
		};

		const result = mapper.props(data);

		expect(result).toMatchObject({
			browsers: [
				{value: 2248, valueKey: 'Chrome'},
				{value: 782, valueKey: 'Firefox'},
				{value: 137, valueKey: 'Chrome Mobile'},
				{value: 136, valueKey: 'Safari'},
				{value: 124, valueKey: 'Mobile Safari'},
				{value: 109, valueKey: 'Edge'},
				{value: 39, valueKey: 'Unknown'},
				{value: 20, valueKey: 'Samsung Browser'},
				{value: 76, valueKey: 'Others'},
			],
			devices: [
				{
					data: [
						{percentage: 0, type: 'Windows', views: 2065},
						{percentage: 0, type: 'macOS', views: 995},
						{percentage: 0, type: 'Linux', views: 136},
						{percentage: 0, type: 'Mac OS X', views: 107},
						{percentage: 0, type: 'Ubuntu', views: 53},
						{percentage: 0, type: 'ChromeOS', views: 3},
						{percentage: 0, type: 'Fedora', views: 2},
						{percentage: 0, type: 'Unknown', views: 1},
					],
					label: 'Desktop',
					percentageOfTotal: 0,
					totalViews: 3362,
					type: 'Desktop',
				},
				{
					data: [
						{percentage: 0, type: 'Android', views: 151},
						{percentage: 0, type: 'iOS', views: 61},
						{percentage: 0, type: 'Unknown', views: 9},
						{percentage: 0, type: 'FreeBSD', views: 8},
						{percentage: 0, type: 'Windows Phone', views: 1},
					],
					label: 'Phone',
					percentageOfTotal: 0,
					totalViews: 230,
					type: 'SmartPhone',
				},
				{
					data: [
						{percentage: 0, type: 'iOS', views: 67},
						{percentage: 0, type: 'Android', views: 12},
					],
					label: 'Tablet',
					percentageOfTotal: 0,
					totalViews: 79,
					type: 'Tablet',
				},
			],
			error: null,
			loading: false,
			total: undefined,
		});
	});

	it('should include segmentId in the request variables when provided', () => {
		const mapper = getDevicesMapper(
			(result) => result.form.submissionsMetric
		);

		const optionsResult = mapper.options({
			router: {
				params: {},
			},
			segmentId: 'segment-100',
		});

		expect(optionsResult.variables).toMatchObject({
			segmentId: 'segment-100',
		});
	});

	it('should not include segmentId in the request variables when not provided', () => {
		const mapper = getDevicesMapper(
			(result) => result.form.submissionsMetric
		);

		const optionsResult = mapper.options({
			router: {
				params: {},
			},
		});

		expect(optionsResult.variables).not.toHaveProperty('segmentId');
	});

	it('should map devices information in correct order', () => {
		const mapper = getDevicesMapper(
			(result) => result.form.submissionsMetric
		);

		const data = {
			data: {
				form: {
					submissionsMetric: {
						browser: [
							{
								value: 9.0,
								valueKey: 'Chrome',
							},
						],
						device: [
							{
								metrics: [
									{
										value: 2.0,
										valueKey: 'Windows',
									},
									{
										value: 1.0,
										valueKey: 'macOS',
									},
								],
								value: 3.0,
								valueKey: 'Desktop',
							},
							{
								metrics: [
									{
										value: 4.0,
										valueKey: 'Android',
									},
								],
								value: 4.0,
								valueKey: 'SmartPhone',
							},
							{
								metrics: [
									{
										value: 1.0,
										valueKey: 'Tizen',
									},
								],
								value: 1.0,
								valueKey: 'Tv',
							},
							{
								metrics: [
									{
										value: 1.0,
										valueKey: 'Linux',
									},
								],
								value: 1.0,
								valueKey: 'SmallScreen',
							},
						],
					},
				},
			},
		};

		const result = mapper.props(data);

		expect(result).toMatchObject({
			browsers: [{value: 9, valueKey: 'Chrome'}],
			devices: [
				{
					data: [
						{percentage: 0, type: 'Windows', views: 2},
						{percentage: 0, type: 'macOS', views: 1},
					],
					label: 'Desktop',
					percentageOfTotal: 0,
					totalViews: 3,
					type: 'Desktop',
				},
				{
					data: [{percentage: 0, type: 'Android', views: 4}],
					label: 'Phone',
					percentageOfTotal: 0,
					totalViews: 4,
					type: 'SmartPhone',
				},
				{
					data: [{percentage: 0, type: 'Tizen', views: 1}],
					label: 'TV',
					percentageOfTotal: 0,
					totalViews: 1,
					type: 'Tv',
				},
				{
					data: [{type: 'Other', views: 1}],
					label: 'Others',
					percentageOfTotal: 0,
					type: 'Other',
				},
			],
			error: null,
			loading: false,
			total: undefined,
		});
	});
});
