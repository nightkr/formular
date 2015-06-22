IN EARLY DEVELOPMENT: DO NOT RELY ON (YET?)

Formulär
========

Formulär is a library for dynamically rendering forms from simple data structures. Care has been taken to make sure that
form definitions and answers can be easily serialized and deserialized using the [Prickle] library, so that validation
and display logic can be shared as much as possible between the client and the server. The library was intended for
cases where the server acts as a dumb store for user-defined forms that are then displayed back for the user.

[Prickle]: https://github.com/benhutchison/prickle