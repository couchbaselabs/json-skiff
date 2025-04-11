# json-skiff

A streaming [JSON Pointer](https://datatracker.ietf.org/doc/html/rfc6901) evaluator.

1. Give it a list of JSON pointers and callbacks.
2. Feed it JSON.
3. Get notified when the target elements are seen.

Less capable, but simpler and faster than the excellent [JsonSurfer](https://github.com/wanglingsong/JsonSurfer) library.

## Usage

### Selecting array elements

Normally, a `-` in a JSON Pointer refers to the last element of an array.
`json-skiff` bends the rules a bit, and interprets `-` as matching _all_ array elements.

Consider this JSON document:

```json
{
  "magicWords": [
    "alakazam",
    "xyzzy"
  ]
}
``` 
A callback associated with `/magicWords/-` is invoked a total of two times: once for `"alakazam"` and once for `"xyzzy"`. 

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

A callback associated with `/widgets/-/serialNumber` is invoked a total of two times: once for `"123"` and once for `"XYZ"`.


## FAQ

### Is this library an officially supported Couchbase product?

No.
This library is not covered by any Couchbase support contract.
