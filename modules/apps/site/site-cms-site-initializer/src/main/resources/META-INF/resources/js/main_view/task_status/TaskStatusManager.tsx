/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DropDown from "@clayui/drop-down";
import Button, {ClayButtonWithIcon} from "@clayui/button";
import React, {useEffect, useRef, useState} from "react";
import '../../../css/components/AssetTaskStatus.scss';
import {ITask} from "./TaskStatusType";
import Badge from "@clayui/badge";
import TaskStatusDropdownItemList
    from "./components/TaskStatusDropdownItemList";
import classnames from "classnames";

function TaskStatusManager({
    items = [],
    totalCount = 0,
}: any) {
    const [active, setActive] = useState(false);
    const [isVisible, setIsVisible] = useState(false);
    const [processingTasks] = useState(totalCount);
    const [tasks] = useState<ITask[]>(items);

    useEffect(() => {
        if (processingTasks > 0) {
            setIsVisible(true);
        }
    }, [processingTasks, setIsVisible]);

    return (
        <>
            {isVisible ? (
                <div className="p-2">
                        <span className="d-flex">
                            {
                                processingTasks > 0 ? (
                                    <DropDown
                                        active={active}
                                        onActiveChange={setActive}
                                        trigger={
                                            <Button
                                                className={classnames({
                                                    "btn-sm border-info text-info pb-1": !active,
                                                    "btn-sm btn-info pb-1": active,
                                                })}
                                                displayType="secondary"
                                            >
                                                <Badge
                                                    className={classnames({
                                                        "mr-2 badge-info": !active,
                                                        "mr-2 badge-light": active,
                                                    })}
                                                    label={processingTasks}
                                                />
                                                {processingTasks === 1
                                                    ? Liferay.Language.get(
                                                        'processing-task')
                                                    : Liferay.Language.get(
                                                        'processing-tasks')
                                                }
                                            </Button>
                                        }
                                        triggerIcon={active ? "caret-top" :
                                            "caret-bottom"}
                                    >
                                        <TaskStatusDropdownItemList items={tasks}/>
                                    </DropDown>
                                ) : (
                                    <Button.Group>
                                        <ClayButtonWithIcon
                                            aria-label="close"
                                            className="btn-sm close-button"
                                            displayType="secondary"
                                            onClick={() => setIsVisible(false)}
                                            symbol="times"
                                            title="close"
                                        />
                                        <DropDown
                                            active={active}
                                            className="task-status-dropdown"
                                            onActiveChange={setActive}
                                            trigger={
                                                <Button
                                                    className="btn-sm"
                                                    displayType="secondary"
                                                >
                                                    {Liferay.Language.get(
                                                        'no-processing-tasks')}
                                                </Button>
                                            }
                                            triggerIcon={active ? "caret-top" :
                                                "caret-bottom"}
                                        >
                                            <TaskStatusDropdownItemList items={tasks}/>
                                        </DropDown>
                                    </Button.Group>
                                )
                            }
                        </span>
                    </div>
                ) : null
            }
        </>
    );
}

export default TaskStatusManager;
