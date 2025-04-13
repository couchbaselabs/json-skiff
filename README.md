# json-skiff

A streaming [JSON Pointer](https://datatracker.ietf.org/doc/html/rfc6901) evaluator in Java.

1. Give it a list of JSON pointers and callbacks.
2. Feed it JSON.
3. Get notified when the target elements are seen.

Less capable, but simpler and faster than the excellent [JsonSurfer](https://github.com/wanglingsong/JsonSurfer) library.

## Maven coordinates

```xml
<dependency>
    <groupId>com.couchbase</groupId>
    <artifactId>json-skiff</artifactId>
    <version>1.0.0</version>
</dependency>
```


## Usage

Imagine you want to parse this very nice JSON document.

```json
{
    "name": "Alice",
    "friends": [
        "White Rabbit",
        "Mad Hatter"
    ]
}
```

Start by building a `JsonStreamParser` and registering callbacks for various JSON pointers.

The argument to a callback is a `MatchedValue`.
In this example we expect the matched values to be JSON Strings, so we use `MatchedValue.readString()` to get the value.

```java
JsonStreamParser parser = JsonStreamParser.builder()
    .doOnValue("/friends/-", it -> System.out.println("Friend: " + it.readString()))
    .doOnValue("/name", it -> System.out.println("Name: " + it.readString()))
    .build();
```

The JSON pointers may be specified in any order -- they don't need to match the order the elements appear in the JSON.
The parser ignores elements that don't match the JSON pointers.

When the very nice JSON document is fed to the parser, you'll see this output:

```text
Name: Alice
Friend: White Rabbit
Friend: Mad Hatter
```

> **NOTE:** It's important to call `parser.close()` (or use a `try-with-resources` statement) when you're done with the parser.


### Feeding from an InputStream

```java
try (InputStream is = createMyJsonInputStream()) {
  parser.feed(is);
  parser.endOfInput();
}
```

If you want, you can feed the parser from additional input streams before calling `parser.endOfInput()`.


### Feeding from a byte array

You can also feed the parser incrementally.
This contrived example feeds the parser every byte of an array, one byte at a time:

```java
byte[] bytes = getMyJsonByteArray();

for (int offset = 0; offset < bytes.length; offset++) {
  int length = 1;
  parser.feed(bytes, offset, length);
}
parser.endOfInput();
```

### Feeding from a java.nio.ByteBuffer

There's an overload of `parser.feed()` that takes a ByteBuffer.


### Selecting array elements

Normally, a `-` in a JSON Pointer refers to the non-existent element past the end of an array.
`json-skiff` bends this rule, and interprets `-` as matching _every_ array element.

In the previous example we used `/friends/-` to select every element of the `friends` array.

The `-` does not need to be the last component of the pointer.
Consider this JSON document:

```json
{
  "widgets": [
    {
      "serialNumber": "123"
    },
    {
      "serialNumber": "XYZ"
    }
  ]
}
```

A callback for `/widgets/-/serialNumber` is invoked once for `"123"` and once for `"XYZ"`.


### Selecting whole Objects and Arrays

If the target field value is a JSON Array or Object, use `MatchedValue.bytes()` to get the bytes of the Array / Object, and use your favorite JSON processing library to parse it.

This is also the recommended way to handle a value whose type is not known at runtime.


### Nullable values

If you expect a matched value might be null, check for null by calling `MatchedValue.isNull()` before calling any of the `read*` methods to avoid `NullPointerException`.


## FAQ

### How to select only the Nth element of an array?

Sorry, this library does not support JSON pointers that reference specific array elements.

### How to do more complex matching?

If a JSON pointer isn't sufficient, take a look at [JsonSurfer](https://github.com/wanglingsong/JsonSurfer) which lets you use JSON Path to specify complex matching rules.

### Is this library an officially supported Couchbase product?

No.
You are welcome to file issues here on GitHub, but Couchbase Technical Support is not responsible for this library, and it's not covered by any Couchbase support contract.
