/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';

import i18n from '../i18n';
import {Liferay} from '../liferay/liferay';
import {removeHTMLTags} from '../utils/string';

const domainRegex = /^(?!:\/\/)([a-zA-Z0-9-_]+?\.)+[a-zA-Z]{2,}$/;

const baseAppSchema = {
	appUsageTermsURL: z.string().url().or(z.literal('')),
	documentationURL: z.string().url().or(z.literal('')),
	installationGuideURL: z.string().url().or(z.literal('')),
	url: z.string().url().or(z.literal('')),
};

const baseContentSchema = z.object({
	description: z.string().min(1).refine(removeHTMLTags),
	title: z.string().min(1),
});

const blocksContentSchemas = {
	textBlock: baseContentSchema,
	textImages: baseContentSchema.extend({
		files: z.array(z.any()).min(1),
	}),
	textVideo: baseContentSchema.extend({
		videoUrl: z.string().url().min(1),
	}),
};

const contentMediaTypeImage = z.object({
	headerImages: z.array(z.any()).min(1),
});

const contentMediaTypeVideo = z.object({
	headerVideoDescription: z.string().optional(),
	headerVideoUrl: z.string().url().min(1),
});

const freeApp = z.object({
	...baseAppSchema,
	email: z.string().email().or(z.literal('')),
	phone: z.string().min(8).or(z.literal('')),
	publisherWebsiteURL: z.string().url().or(z.literal('')),
});

const paidApp = z.object({
	...baseAppSchema,
	email: z.string().email(),
	phone: z.string().min(8),
	publisherWebsiteURL: z.string().url(),
});

const resources = z.object({
	free: z.number(),
	limit: z.number(),
	used: z.number(),
});

const rootProjectPlanUsage = z.object({
	cpu: resources,
	instance: resources,
	memory: resources,
});

const zodSchema = {
	accountCreator: z.object({
		accounts: z.any().array().optional(),
		companyName: z
			.string()
			.min(1, {message: 'Please enter a company name to continue'}),
		country: z
			.string()
			.min(2, {message: 'Please select the country to continue'}),
		emailAddress: z
			.string()
			.email(i18n.translate('this-field-is-required')),
		extension: z.string().optional(),
		familyName: z
			.string()
			.min(3, {message: i18n.translate('this-field-is-required')}),
		givenName: z.string(),
		phone: z.object({
			code: z.string(),
			flag: z.string(),
		}),
		phoneNumber: z
			.string()
			.min(1, {message: 'Please enter a phone number to continue.'}),
	}),
	analyticsProvisioning: z.object({
		_refAllowedEmailDomains: z.array(z.any()),
		_refIncidentReportContacts: z.array(z.any()),
		acceptTerms: z.boolean().refine((value) => value, {
			message: 'You must agree with the terms',
		}),
		allowedEmailDomains: z
			.array(z.string())
			.optional()
			.default([])
			.refine(
				(values) =>
					values.length
						? values.every((value) => domainRegex.test(value))
						: true,
				'One of the chosen domains is invalid.'
			),
		dataCenterLocation: z.string(),
		friendlyWorkspaceURL: z.string().optional(),
		incidentReportContacts: z.array(z.string().email()).min(1),
		region: z.string(),
		timezone: z.string(),
		workspaceName: z.string().min(3),
		workspaceOwnerEmail: z
			.string()
			.default(Liferay.ThemeDisplay.getUserEmailAddress()),
	}),
	appPublishing: {
		build: z.object({
			appType: z.string(),
			liferayPackages: z
				.array(
					z.object({
						file: z.object({}),
						versions: z.array(z.string()).min(1),
					})
				)
				.min(1),
		}),
		profile: z.object({
			areas: z.array(z.any()).nonempty(),
			categories: z.object({label: z.string(), value: z.string().min(1)}),
			description: z.string().min(3),
			name: z.string().min(3),
			tags: z.array(z.any()).nonempty(),
		}),
		storefront: z.object({images: z.array(z.any()).min(1).max(10)}),
		support: {
			supportForFreeApp: freeApp,
			supportForPaidApp: paidApp,
		},
		termsAndConditions: z.boolean().refine((data) => data === true),
		version: z.object({
			notes: z.string(),
			version: z.string(),
		}),
	},
	becomePublisherForm: z.object({
		emailAddress: z.string().email('Please fill in valid email'),
		extension: z.string().optional(),
		firstName: z.string().min(3, 'First name is required'),
		lastName: z.string().min(3, 'Last name is required'),
		phone: z
			.object({
				code: z.string(),
				flag: z.string(),
			})
			.optional(),
		phoneNumber: z
			.string()
			.min(1, {message: i18n.translate('this-field-is-required')}),
		publisherType: z.array(z.string()).min(1),
		requestDescription: z
			.string()
			.min(3, {message: 'Request Description is required'}),
	}),
	billingAddress: z.object({
		city: z.string().min(1),
		country: z.string().min(1),
		countryISOCode: z.string().optional(),
		name: z.string().min(1),
		phoneNumber: z.string().min(1),
		regionISOCode: z.string().optional(),
		street1: z.string().min(1),
		street2: z.string().optional(),
		zip: z.string().min(1),
	}),
	contactSales: z.object({
		accountName: z
			.string()
			.min(3, i18n.sub('x-is-required', 'account-name')),
		additionalAppsRequested: z.string(),
		comments: z.string(),
		email: z.string().email(i18n.translate('please-fill-in-a-valid-email')),
		name: z.string().min(3, i18n.sub('x-is-required', 'name')),
	}),
	generateLicenseKey: z.object({
		description: z.string().max(100, {message: 'Invalid license name'}),
		hostname: z.string(),
		ipAddress: z.string(),
		macAddress: z.string(),
		subscription: z
			.object({
				name: z.string(),
				productPurchasedKey: z.string(),
				productVersion: z.string(),
				skuId: z.number(),
			})
			.optional(),
	}),
	installProductSchema: z.object({
		environment: z.object({
			isExtensionEnvironment: z.boolean(),
			projectId: z.string(),
		}),
		project: z.object({
			availabilityToProduct: z.boolean(),
			environments: z.array(
				z.object({
					isExtensionEnvironment: z.boolean(),
					projectId: z.string(),
				})
			),
			rootProjectId: z.string(),
			rootProjectPlanUsage,
		}),
	}),
	invitedNewMember: z.object({
		emailAddress: z
			.string()
			.min(5, 'Please enter an email')
			.email('Invalid email address'),
		firstName: z.string().min(3, 'Please enter member name'),
		lastName: z.string().min(3, 'Last name is required'),
		roles: z.string().array().min(5, 'Please select at least one role'),
	}),
	solutionPublishing: {
		company: z
			.object({
				description: z.string().min(1),
				email: z.string().email().min(1),
				phone: z.string().min(1),
				website: z.string().min(1),
			})
			.refine((data) => !!removeHTMLTags(data.description)),
		contactUs: z.string().email().min(1),
		details: z
			.array(
				z.object({
					content: z.lazy(() =>
						z.union([
							blocksContentSchemas.textBlock,
							blocksContentSchemas.textImages,
							blocksContentSchemas.textVideo,
						])
					),
					type: z.enum([
						'text-block',
						'text-images-block',
						'text-video-block',
					]),
				})
			)
			.min(2),
		header: z
			.object({
				contentType: z.object({
					content: z.lazy(() =>
						z.union([contentMediaTypeImage, contentMediaTypeVideo])
					),
					type: z.enum(['embed-video-url', 'upload-images']),
				}),
				description: z.string().min(1),
				title: z.string().min(1),
			})
			.refine((data) => !!removeHTMLTags(data.description)),
		profile: z.object({
			categories: z.array(z.any()).nonempty(),
			description: z.string().min(3),
			name: z.string().min(3),
			tags: z.array(z.any()).nonempty(),
		}),
		termsAndConditions: z.boolean().refine((data) => data === true),
	},
	trialForm: z.object({
		accountId: z.string().optional(),
		consoleInviteEmailAddresses: z.array(z.string().email()),
		product: z
			.any()
			.refine((value) => !!value, {message: 'Product is required'}),
		sendNotificationEmail: z.boolean(),
	}),
};

export {z, zodResolver};

export default zodSchema;
