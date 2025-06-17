/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReactNode, createContext, useContext, useReducer} from 'react';
import {useParams} from 'react-router-dom';
import useSWR from 'swr';

import {UploadedFile} from '../components/FileList/FileList';
import Loading from '../components/Loading';
import {MarketplaceProduct} from '../entity/MarketplaceProduct';
import {
	ProductLicenseTier,
	ProductLicenseType,
	ProductPriceModel,
	ProductSpecificationKey,
	ProductTags,
	ProductType,
	ProductVocabulary,
} from '../enums/Product';
import {useGetVocabulariesAndCategories} from '../hooks/data/useGetVocabulariesAndCategories';
import HeadlessCommerceAdminCatalogImpl from '../services/rest/HeadlessCommerceAdminCatalog';
import {getRandomID} from '../utils/string';

export type LicensePrice = {key: number; value: number};
export type LicenseType = 'Perpetual' | 'Subscription';

export type LicensingPrices = {
	[currency: string]: {
		developer?: {
			[key: number]: number;
		};
		standard: {
			[key: number]: number;
		};
		trial?: undefined;
	};
};

export type PriceEntry = {
	hasTierPrice: boolean;
	id: number;
	price: number;
	priceEntryId?: number;
	priceListId: number;
	sku: string;
	skuExternalReferenceCode: string;
	skuId: number;
};

export enum NewAppTypes {
	SET_BUILD = 'SET_BUILD',
	SET_CLEANUP = 'SET_CLEANUP',
	SET_CONTEXT = 'SET_CONTEXT',
	SET_DELETE_IMAGE = 'SET_DELETE_IMAGE',
	SET_LICENSING = 'SET_LICENSING',
	SET_LICENSING_ADD_PRICE = 'SET_LICENSING_ADD_PRICE',
	SET_LICENSING_DELETE_CURRENCY = 'SET_LICENSING_DELETE_CURRENCY',
	SET_LICENSING_DELETE_PRICE = 'SET_LICENSING_DELETE_PRICE',
	SET_LICENSING_UPDATE_PRICES = 'SET_LICENSING_UPDATE_PRICES',
	SET_LOADING = 'SET_LOADING',
	SET_PRICING = 'SET_PRICING',
	SET_PRODUCT = 'SET_PRODUCT',
	SET_PRODUCT_ID = 'SET_PRODUCT_ID',
	SET_PROFILE = 'SET_PROFILE',
	SET_STOREFRONT = 'SET_STOREFRONT',
	SET_SUPPORT = 'SET_SUPPORT',
	SET_TERMS_AND_CONDITIONS = 'SET_TERMS_AND_CONDITIONS',
	SET_VERSION = 'SET_VERSION',
}

export type NewAppInitialState = {
	_product?: Product;
	build: {
		appType: ProductType;

		liferayPackages: {
			file: any;
			id: string;
			versions: string[];
		}[];
		resourceRequirements: {
			cpu?: string;
			ram?: string;
		};
	};
	catalog: Catalog;
	licensing: {
		licenseType: LicenseType;
		prices: LicensingPrices;
		trial30Day: boolean;
	};
	loading: boolean;
	pricing: {
		priceModel: 'Free' | 'Paid';
	};
	productId: number;
	profile: {
		areas: {
			label: string;
			value: string;
		}[];
		categories: {
			label: string;
			value: string;
		};
		description: string;
		file: UploadedFile;
		name: string;
		tags: {
			label: string;
			value: string;
		}[];
	};
	references: {
		flags: {
			canModifyProductProfileCategory: boolean;
		};
		imagesToDelete: string[];
		vocabulariesAndCategories: any;
	};
	storefront: {
		images: UploadedFile[];
		video: {description?: string; videoURL?: string};
	};
	support: {
		appUsageTermsURL: string;
		documentationURL: string;
		email: string;
		installationGuideURL: string;
		phone: string;
		publisherWebsiteURL: string;
		url: string;
	};
	termsAndConditions: boolean;
	version: {
		notes: string;
		version: string;
	};
};

type NewAppPayload = {
	[NewAppTypes.SET_BUILD]: Partial<NewAppInitialState['build']>;
	[NewAppTypes.SET_CLEANUP]: undefined;
	[NewAppTypes.SET_CONTEXT]: Product;
	[NewAppTypes.SET_DELETE_IMAGE]: string;
	[NewAppTypes.SET_LICENSING]: Partial<NewAppInitialState['licensing']>;
	[NewAppTypes.SET_LICENSING_ADD_PRICE]: {
		currency: string;
		licenseTier: ProductLicenseTier;
	};
	[NewAppTypes.SET_LICENSING_DELETE_CURRENCY]: {
		currency: string;
	};
	[NewAppTypes.SET_LICENSING_DELETE_PRICE]: {
		currency: string;
		key: number;
		licenseTier: ProductLicenseTier;
	};
	[NewAppTypes.SET_LICENSING_UPDATE_PRICES]: {
		currency: string;
		index: number;
		licenseTier: ProductLicenseTier;
		price: number;
		quantity: number;
	};
	[NewAppTypes.SET_LOADING]: boolean;
	[NewAppTypes.SET_PRICING]: Partial<NewAppInitialState['pricing']>;
	[NewAppTypes.SET_PRODUCT]: Product;
	[NewAppTypes.SET_PRODUCT_ID]: number;
	[NewAppTypes.SET_PROFILE]: Partial<NewAppInitialState['profile']>;
	[NewAppTypes.SET_STOREFRONT]: Partial<NewAppInitialState['storefront']>;
	[NewAppTypes.SET_SUPPORT]: Partial<NewAppInitialState['support']>;
	[NewAppTypes.SET_TERMS_AND_CONDITIONS]: boolean;
	[NewAppTypes.SET_VERSION]: Partial<NewAppInitialState['version']>;
};

const newAppInitialState: NewAppInitialState = {
	build: {
		appType: null as unknown as ProductType,
		liferayPackages: [],
		resourceRequirements: {
			cpu: '',
			ram: '',
		},
	},
	catalog: {} as Catalog,
	licensing: {
		licenseType: ProductLicenseType.PERPETUAL,
		prices: {
			USD: {
				standard: {
					1: 0,
				},
			},
		},
		trial30Day: false,
	},
	loading: false,
	pricing: {
		priceModel: ProductPriceModel.FREE,
	},
	productId: 0,
	profile: {
		areas: [],
		categories: {label: '', value: ''},
		description: '',
		file: {} as UploadedFile,
		name: '',
		tags: [],
	},
	references: {
		flags: {canModifyProductProfileCategory: false},
		imagesToDelete: [],
		vocabulariesAndCategories: {},
	},
	storefront: {images: [], video: {}},
	support: {
		appUsageTermsURL: '',
		documentationURL: '',
		email: '',
		installationGuideURL: '',
		phone: '',
		publisherWebsiteURL: '',
		url: '',
	},
	termsAndConditions: false,
	version: {
		notes: '',
		version: '1.0',
	},
};

export type AppActions =
	ActionMap<NewAppPayload>[keyof ActionMap<NewAppPayload>];

const filterProductVocabularies = (product: Product, vocabulary: string) =>
	product.categories
		.filter(
			(category) =>
				category.vocabulary.toLowerCase() === vocabulary.toLowerCase()
		)
		.map(({id, name}) => ({label: name, value: `${id}`}));

const reducer = (state: NewAppInitialState, action: AppActions) => {
	switch (action.type) {
		case NewAppTypes.SET_BUILD: {

			// Reset the Liferay Packages if the App Type is changed

			if (
				action.payload.appType &&
				action.payload.appType !== state.build.appType
			) {
				state.build.liferayPackages = [];
			}

			return {
				...state,
				build: {
					...state.build,
					...action.payload,
				},
			};
		}

		case NewAppTypes.SET_DELETE_IMAGE: {
			return {
				...state,
				references: {
					...state.references,
					imagesToDelete: [
						...(state?.references?.imagesToDelete || []),
						action.payload,
					],
				},
			};
		}

		case NewAppTypes.SET_LOADING: {
			return {...state, loading: action.payload};
		}

		case NewAppTypes.SET_PRODUCT_ID: {
			return {
				...state,
				productId: action.payload,
			};
		}

		case NewAppTypes.SET_CONTEXT: {
			const newState = {...state};
			const _product = action.payload;
			const productSpecifications = _product.productSpecifications || [];

			const specificationsMap = new Map<string, string>();

			for (const productSpecification of productSpecifications) {
				specificationsMap.set(
					productSpecification.specificationKey,
					productSpecification.value.en_US || ''
				);
			}

			const storeFrontImages = (_product.images ?? []).filter(
				({tags}) => !tags?.includes('app icon')
			);

			const appIcon = (_product.images ?? []).find(({tags}) =>
				tags?.includes(ProductTags.SOLUTION_PROFILE_APP_ICON)
			);

			const liferayPackages = _product.productVirtualSettings
				? _product.productVirtualSettings.productVirtualSettingsFileEntries.map(
						(fileEntry) => {
							return {
								file: {
									error: false,
									fileName: fileEntry.src,
									id: getRandomID(),
									readableSize: '',
									src: fileEntry.src,
								},
								id: getRandomID(),
								versions: fileEntry.version.split(','),
							};
						}
					)
				: [];

			const categories = filterProductVocabularies(
				_product,
				ProductVocabulary.APP_CATEGORY
			)[0];

			return {
				...state,
				...newState,
				_product,
				build: {
					...newState.build,
					appType: specificationsMap.get(
						ProductSpecificationKey.APP_TYPE
					),
					compatibleOffering: [],
					liferayPackages,
					resourceRequirements: {
						cpu: specificationsMap.get(
							ProductSpecificationKey.APP_BUILD_NUMBER_OF_CPUS
						),
						ram: specificationsMap.get(
							ProductSpecificationKey.APP_BUILD_RAM_IN_GBS
						),
					},
				} as NewAppInitialState['build'],
				licensing: {
					...newState.licensing,
					licenseType: specificationsMap.get(
						ProductSpecificationKey.APP_LICENSING_TYPE
					),
				} as NewAppInitialState['licensing'],
				pricing: {
					...newState.pricing,
					priceModel: specificationsMap.get(
						ProductSpecificationKey.APP_PRICING_MODEL
					),
				} as NewAppInitialState['pricing'],
				profile: {
					...newState.profile,
					areas: filterProductVocabularies(
						_product,
						ProductVocabulary.APP_AREA
					),
					categories,
					description: _product.description.en_US,
					file: {
						changed: false,
						fileName: appIcon?.title?.en_US as string,
						id: appIcon?.externalReferenceCode as unknown as string,
						preview: _product.thumbnail,
						progress: 100,
						uploaded: true,
					},
					name: _product.name.en_US,
					tags: filterProductVocabularies(
						_product,
						ProductVocabulary.APP_TAGS
					),
				} as NewAppInitialState['profile'],
				references: {
					...state.references,
					flags: {
						...state.references,
						canModifyProductProfileCategory:
							categories === undefined,
					},
				} as NewAppInitialState['references'],
				storefront: {
					...newState.storefront,
					images: storeFrontImages.map(
						({externalReferenceCode, src, title}) => ({
							changed: false,
							fileName: title.en_US,
							id: externalReferenceCode,
							imageDescription: title.en_US,
							preview: new URL(src).pathname,
							progress: 100,
							uploaded: true,
						})
					),
					video: {
						description: specificationsMap.get(
							ProductSpecificationKey.APP_STOREFRONT_VIDEO_DESCRIPTION
						),
						videoURL: specificationsMap.get(
							ProductSpecificationKey.APP_STOREFRONT_VIDEO_URL
						),
					},
				} as NewAppInitialState['storefront'],
				support: {
					...newState.support,
					appUsageTermsURL:
						specificationsMap.get(
							ProductSpecificationKey.APP_SUPPORT_USAGE_TERMS_URL
						) ?? '',
					documentationURL:
						specificationsMap.get(
							ProductSpecificationKey.APP_SUPPORT_DOCUMENTATION_URL
						) ?? '',
					email:
						specificationsMap.get(
							ProductSpecificationKey.APP_SUPPORT_EMAIL
						) ?? '',
					installationGuideURL:
						specificationsMap.get(
							ProductSpecificationKey.APP_SUPPORT_INSTALLATION_GUIDE_URL
						) ?? '',
					phone:
						specificationsMap.get(
							ProductSpecificationKey.APP_SUPPORT_PHONE
						) ?? '',
					publisherWebsiteURL:
						specificationsMap.get(
							ProductSpecificationKey.APP_SUPPORT_PUBLISHER_WEBSITE_URL
						) ?? '',
					url:
						specificationsMap.get(
							ProductSpecificationKey.APP_SUPPORT_URL
						) ?? '',
				} as NewAppInitialState['support'],
				version: {
					notes: specificationsMap.get(
						ProductSpecificationKey.APP_VERSION_NOTES
					),
					version: specificationsMap.get(
						ProductSpecificationKey.APP_VERSION
					),
				} as NewAppInitialState['version'],
			};
		}

		case NewAppTypes.SET_PROFILE: {
			return {
				...state,
				profile: {
					...state.profile,
					...action.payload,
				},
			};
		}

		case NewAppTypes.SET_PRODUCT: {
			return {
				...state,
				_product: action.payload,
			};
		}

		case NewAppTypes.SET_VERSION: {
			return {
				...state,
				version: {
					...state.version,
					...action.payload,
				},
			};
		}

		case NewAppTypes.SET_PRICING: {
			return {
				...state,
				pricing: {
					...state.pricing,
					...action.payload,
				},
			};
		}

		case NewAppTypes.SET_SUPPORT: {
			return {
				...state,
				support: {
					...state.support,
					...action.payload,
				},
			};
		}

		case NewAppTypes.SET_STOREFRONT: {
			return {
				...state,
				storefront: {
					...state.storefront,
					...action.payload,
				},
			};
		}

		case NewAppTypes.SET_LICENSING: {
			return {
				...state,
				licensing: {
					...state.licensing,
					...action.payload,
				},
			};
		}

		case NewAppTypes.SET_LICENSING_ADD_PRICE: {
			const {currency, licenseTier} = action.payload;

			const oldPrices = state.licensing.prices;

			const currentPricesForCurrency = oldPrices[currency] || {};
			const currentPricesForTier =
				currentPricesForCurrency[licenseTier] || {};

			const newKey = Object.keys(currentPricesForTier).length
				? Math.max(...Object.keys(currentPricesForTier).map(Number)) + 1
				: 1;

			const updatedPrices = {
				...oldPrices,
				[currency]: {
					...currentPricesForCurrency,
					[licenseTier]: {
						...currentPricesForTier,
						[newKey]: 0,
					},
				},
			};

			return {
				...state,
				licensing: {
					...state.licensing,
					prices: updatedPrices,
				},
			};
		}

		case NewAppTypes.SET_LICENSING_DELETE_PRICE: {
			const {currency, key, licenseTier} = action.payload;

			const oldPrices = state.licensing.prices;

			if (!oldPrices[currency] || !oldPrices[currency][licenseTier]) {
				return state;
			}

			const updatedLicenseTierPrices = {
				...oldPrices[currency][licenseTier],
			};

			if (key in updatedLicenseTierPrices) {
				delete (updatedLicenseTierPrices as any)[key];
			}

			return {
				...state,
				licensing: {
					...state.licensing,
					prices: {
						...oldPrices,
						[currency]: {
							...oldPrices[currency],
							[licenseTier]: updatedLicenseTierPrices,
						},
					},
				},
			};
		}

		case NewAppTypes.SET_LICENSING_DELETE_CURRENCY: {
			const {currency} = action.payload;

			const updatedPrices = {...state.licensing.prices};
			delete updatedPrices[currency];

			return {
				...state,
				licensing: {
					...state.licensing,
					prices: updatedPrices,
				},
			};
		}

		case NewAppTypes.SET_LICENSING_UPDATE_PRICES: {
			const {currency, index, licenseTier, price, quantity} =
				action.payload;

			const currentLicensePrice = state.licensing.prices;

			const updatedPrices = {
				...(currentLicensePrice[currency]?.[licenseTier] || {}),
			};

			if (quantity && quantity !== index) {
				delete updatedPrices[index];
			}

			updatedPrices[quantity] = price;

			return {
				...state,
				licensing: {
					...state.licensing,
					prices: {
						...currentLicensePrice,
						[currency]: {
							...currentLicensePrice[currency],
							[licenseTier]: updatedPrices,
						},
					},
				},
			};
		}

		case NewAppTypes.SET_TERMS_AND_CONDITIONS: {
			return {...state, termsAndConditions: action.payload};
		}

		default:
			return state;
	}
};

export const NewAppContext = createContext<
	[NewAppInitialState, React.Dispatch<AppActions>]
>([newAppInitialState, () => null]);

type NewAppContextProviderProps = {
	catalog: Catalog;
	children: ReactNode;
};

export default function NewAppContextProvider({
	catalog,
	children,
}: NewAppContextProviderProps) {
	const [state, dispatch] = useReducer(reducer, newAppInitialState);
	const {productId} = useParams();
	const {data = {}, isLoading: isLoadingVocabularies} =
		useGetVocabulariesAndCategories([
			ProductVocabulary.APP_AREA,
			ProductVocabulary.APP_CATEGORY,
			ProductVocabulary.APP_TAGS,
			ProductVocabulary.LIFERAY_PLATFORM_OFFERING,
			ProductVocabulary.PRODUCT_TYPE,
		]);

	const {data: product, isLoading} = useSWR(
		productId ? `/product/${productId}` : null,
		() =>
			HeadlessCommerceAdminCatalogImpl.getProduct(
				productId as string,
				new URLSearchParams({
					nestedFields:
						'attachments,catalog,images,productSpecifications,productOptions,productVirtualSettings,skus',
				})
			),
		{
			onSuccess: (data) => {
				dispatch({
					payload: data,
					type: NewAppTypes.SET_CONTEXT,
				});
			},
		}
	);

	useSWR(
		product ? `/product/prices/${productId}` : null,
		() => new MarketplaceProduct(product!).getProductPrices(),
		{
			onSuccess: (prices) =>
				dispatch({
					payload: {prices: prices as unknown as LicensingPrices},
					type: NewAppTypes.SET_LICENSING,
				}),
		}
	);

	if (isLoadingVocabularies) {
		return <Loading />;
	}

	return (
		<NewAppContext.Provider
			value={[
				{
					...state,
					catalog,
					loading: isLoadingVocabularies || isLoading,
					references: {
						...state.references,
						vocabulariesAndCategories: data,
					},
				},
				dispatch,
			]}
		>
			{state.loading && (
				<Loading.FullScreen>
					Hang tight, the submission of <b>{state.profile.name}</b> is
					being sent to <b>Liferay</b>
				</Loading.FullScreen>
			)}

			{children}
		</NewAppContext.Provider>
	);
}

export function useNewAppContext() {
	return useContext(NewAppContext);
}
