## JavaLocalServiceImplUserIdUsageCheck

Do not use `GuestOrUserUtil.getUserId()` or `PrincipalThreadLocal.getUserId()` in `*LocalServiceImpl.java`.

For more information on how to fix, see
<https://github.com/brianchandotcom/liferay-portal/pull/164945/commits/d6068a7302c6d3e186c12df441cb708e1649a896>.