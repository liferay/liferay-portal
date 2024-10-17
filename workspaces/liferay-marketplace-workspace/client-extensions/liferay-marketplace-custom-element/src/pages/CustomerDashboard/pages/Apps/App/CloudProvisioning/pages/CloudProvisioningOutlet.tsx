/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMemo} from 'react';
import {UseFormReturn, useForm} from 'react-hook-form';
import {
	NavigateFunction,
	Outlet,
	useLocation,
	useNavigate,
	useParams,
} from 'react-router-dom';

import {PageRenderer} from '../../../../../../../components/Page';
import ProductPurchase from '../../../../../../../components/ProductPurchase';
import {useAccount} from '../../../../../../../hooks/data/useAccounts';
import useGetProductByOrderId from '../../../../../../../hooks/useGetProductByOrderId';
import i18n from '../../../../../../../i18n';
import zodSchema, {z, zodResolver} from '../../../../../../../schema/zod';
import useGetResourceInfo, {
	convertMegabyteToGigabyte,
} from '../../../../../../GetApp/hooks/useGetResourceInfo';

import '../index.scss';
import consoleOAuth2 from '../../../../../../../services/oauth/Console';
import {ConsoleUserProject} from '../../../../../../../services/oauth/types';
import {scrollToTop} from '../../../../../../../utils/browser';

const verifyAvailabilityToInstall = (
	userProject: any,
	productRequirements: any
) => {
	const availableCPU = userProject?.rootProjectPlanUsage?.cpu?.free;
	const availableRAM = userProject?.rootProjectPlanUsage?.memory?.free;

	return (
		availableCPU >= productRequirements?.cpu &&
		availableRAM >=
			convertMegabyteToGigabyte({value: productRequirements?.ram})
	);
};

const SelectedProjectBanner: React.FC<{project: any}> = ({project}) => (
	<>
		<hr />

		<div className="align-items-center d-flex justify-content-between">
			<small className="font-weight-bold">
				{i18n.translate('selected-project')}
			</small>

			<span className="align-items-end d-flex flex-column">
				<small className="font-weight-bold m-0">
					{project?.rootProjectId.toUpperCase()}
				</small>

				<small className="subscription-banner-text text-nowrap">
					{`${project?.environments.length} environments, ${project?.rootProjectPlanUsage.cpu.free} CPUs, ${convertMegabyteToGigabyte(
						{
							inverseOperation: true,
							value: project?.rootProjectPlanUsage.memory.free,
						}
					)} GB RAM`}
				</small>
			</span>
		</div>
	</>
);

const getProductRequirements = (product: DeliveryProduct) => {
	const requirements = {
		cpu: 0,
		ram: 0,
	};

	for (const requirement in requirements) {
		const currentSpecification = product?.productSpecifications.find(
			(specification) => specification.specificationKey === requirement
		);

		(requirements as any)[requirement] = currentSpecification?.value;
	}

	return requirements;
};

const steps = [
	{key: '', title: i18n.translate('project')},
	{key: 'environment', title: i18n.translate('environment')},
	{
		key: 'installation',
		title: i18n.translate('installation'),
	},
];

const CloudProvisioningOutlet = () => {
	const {data: selectedAccount} = useAccount();
	const {orderId} = useParams();
	const {pathname} = useLocation();
	const navigate = useNavigate();
	const orderInfo = useGetProductByOrderId(orderId as string);

	const {data, error, isLoading} = orderInfo;
	const product = data?.product as DeliveryProduct;

	const form = useForm<z.infer<typeof zodSchema.installProductSchema>>({
		resolver: zodResolver(zodSchema.installProductSchema),
	});

	const resourceResponse = useGetResourceInfo({
		product,
		selectedProject: undefined,
		shouldFetch: true,
	});

	const placedOrder = data?.placedOrder as PlacedOrder;

	const productRequirements = getProductRequirements(product);

	const projects = useMemo(
		() =>
			resourceResponse?.resourceRequest?.userProjects.map(
				(userProject) => ({
					...userProject,
					availabilityToProduct: verifyAvailabilityToInstall(
						userProject,
						productRequirements
					),
				})
			) ?? [],
		[resourceResponse?.resourceRequest?.userProjects, productRequirements]
	);

	const path = pathname.split('/').at(-1) as string;

	const project = form.watch('project');

	const onSubmit = async ({
		environment,
	}: z.infer<typeof zodSchema.installProductSchema>) => {
		scrollToTop();

		navigate('installation');

		await consoleOAuth2.provisioning(placedOrder.id, {
			orderItemId: placedOrder.placedOrderItems[0].id,
			projectId: environment.projectId,
		});
	};

	return (
		<PageRenderer error={error} isLoading={isLoading}>
			<ProductPurchase>
				<ProductPurchase.Header
					product={product}
					rightNode={
						<div className="d-flex flex-column">
							<div>Standard License</div>

							<small className="d-flex justify-content-end">
								{`${productRequirements.cpu}CPUs, ${productRequirements.ram}GB RAM`}
							</small>
						</div>
					}
				>
					{project && <SelectedProjectBanner project={project} />}
				</ProductPurchase.Header>

				<ProductPurchase.Steps
					activeKey={path === 'install' ? '' : path}
					className="mt-5 px-8"
					steps={steps}
				/>

				<ProductPurchase.Body>
					<Outlet
						context={{
							form,
							navigate,
							onClickCancel: () =>
								navigate(
									`../order/${orderId}/cloud-provisioning`
								),
							onSubmit: form.handleSubmit(onSubmit),
							orderId,
							placedOrder: orderInfo.data?.placedOrder,
							projects,
							selectedAccount,
						}}
					/>
				</ProductPurchase.Body>
			</ProductPurchase>
		</PageRenderer>
	);
};

type ConsoleUserProjectWithExtension = ConsoleUserProject & {
	availabilityToProduct: boolean;
};

export type CloudProvisioningOutletContext = {
	form: UseFormReturn<z.infer<typeof zodSchema.installProductSchema>>;
	navigate: NavigateFunction;
	onClickCancel: () => void;
	onSubmit: () => void;
	orderId: number;
	placedOrder: any;
	projects: ConsoleUserProjectWithExtension[];
	selectedAccount: ReturnType<typeof useAccount>;
};

export default CloudProvisioningOutlet;
