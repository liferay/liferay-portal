## EarlyLoopExitCheck

When an if-statement is the last statement of a loop body, invert its condition
and exit early instead of nesting the rest of the loop body in it.

### Example

Instead of:

```java
public void sendEmails(List<User> users) {
    for (User user : users) {
        if (user.isActive()) {
            sendEmail(user);
        }
    }
}
```

We should do:

```java
public void sendEmails(List<User> users) {
    for (User user : users) {
        if (!user.isActive()) {
            continue;
        }

        sendEmail(user);
    }
}
```