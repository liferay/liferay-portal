## PrintWriterWrapperCheck

Do not wrap a `Writer` or an `OutputStream` in a `PrintWriter` or an `UnsyncPrintWriter` only to print to it. The wrapper swallows every `IOException`, so a failed write goes unnoticed, it adds an allocation, and when it is constructed with `autoFlush` set to `true` it flushes the wrapped writer on every `println`.

Write to the wrapped `Writer` or to the response writer directly. For an `OutputStream`, use an `OutputStreamWriter` with an explicit charset.

### Example

Incorrect:

```java
PrintWriter printWriter = new PrintWriter(
    httpServletResponse.getOutputStream(), true);

printWriter.write(json);

printWriter.close();
```

Correct:

```java
PrintWriter printWriter = httpServletResponse.getWriter();

printWriter.write(json);
```

The check flags a new `PrintWriter` or `UnsyncPrintWriter` that is assigned to a local variable, a field or a try-with-resources resource, or passed to a private method of the same class, when that variable or parameter is only used to call `append`, `close`, `flush`, `print`, `println` or `write`. It also flags one assigned to a variable declared as `Writer`, since the `PrintWriter` methods are never reachable through it.

A writer that is passed to another method, returned, assigned to another variable, or used to call `checkError`, `format` or `printf` is not flagged. Test code, `modules/sdk`, `modules/third-party` and `modules/util` are not checked.