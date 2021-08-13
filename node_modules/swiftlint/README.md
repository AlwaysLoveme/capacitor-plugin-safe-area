# swiftlint

Tiny [SwiftLint](https://github.com/realm/SwiftLint) wrapper for npm. SwiftLint [must still be installed](https://github.com/realm/SwiftLint#installation) and `swiftlint` must be on your PATH.

Invocations of `node-swiftlint` on Windows simply print a warning and pass.

This package supports [cosmiconfig](https://github.com/davidtheclark/cosmiconfig) like Prettier does, instead of just `.swiftlint.yml`.

## Usage

```
npm install -g swiftlint
```

Run `node-swiftlint` to invoke the wrapper.
