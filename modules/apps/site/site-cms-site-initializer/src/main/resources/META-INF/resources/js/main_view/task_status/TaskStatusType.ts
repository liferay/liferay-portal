/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
import ClayIcon from "@clayui/icon";
import LoadingIndicator from "@clayui/loading-indicator";
import React from 'react';

export interface ITask {
    actionName: string,
    dateCreated: string,
    dateModified: string,
    id: number,
    taskItems: number,
    taskResult: string,
    totalCount: number,
}

export const COMPLETED = 'completed';
export const FAILED = 'failed';
export const PROCESSING = 'processing';

export const STATUS_PROPERTIES: Record<string, TStatusProperties> = {
    [COMPLETED]: {
        component: ClayIcon,
        icon: 'check-circle-full',
        iconClassName: 'text-success',
        labelDisplayType: 'success',
    },
    [FAILED]: {
        component: ClayIcon,
        icon: 'times-circle-full',
        iconClassName: 'text-danger',
        labelDisplayType: 'danger',
    },
    [PROCESSING]: {
        component: LoadingIndicator,
        iconClassName: 'loading-animation',
        displayType: 'primary',
        labelDisplayType: 'info',
    },
};

export type TStatusProperties = {
    component: React.ComponentType<any>;
    displayType?: string,
    icon?: string;
    iconClassName: string;
    labelDisplayType: 'danger' | 'info' | 'success';
};
