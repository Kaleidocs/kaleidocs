# kaleidocs

An Angular web app designed to ... well, that part is up to you.

## Installation

Pull all Leiningen, npm and bower dependencies to your machine:

```bash
lein bower install
lein npm install
lein deps
```

## Unit tests

### Live coding

Have your files watched and auto-compiled:

```bash
lein cl2c auto dev
```
This will watch for changes and re-compile `*.cl2` files to Javascript.

Now open an other terminal, have [testem][1] run the tests automatically:

```bash
node_modules/testem/testem.js
```

[1]: https://github.com/airportyh/testem

### Run the tests once

If you just want to run the tests once:

```bash
lein cl2c once dev
node_modules/testem/testem.js ci
```

## Front-end development

You may want not only unit tests but also the final HTML file to
reload on update. Tools like [nodefront][1] is for you:

[1]: http://karthikv.github.io/nodefront/

```bash
# Auto compile front-end
lein cl2c auto prod
```

```bash
# install nodefront if you haven't
npm install -g nodefront
cd resources/public
nodefront serve --live
```

# Usage

FIXME

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
