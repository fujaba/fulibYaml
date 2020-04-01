# fulibYaml v1.0.0

# fulibYaml v1.0.1

# fulibYaml v1.0.2

# fulibYaml v1.0.3

# fulibYaml v1.1.0

* Bumped version number.

# fulibYaml v1.2.0

## New Features

+ Added the `ReflectorMap.canReflect` and `.discoverObjects` methods.
+ Added the `Reflector.getOwnProperties` and `.getAllProperties` methods.
+ Added the `YamlGenerator` class, a more streamlined API for converting events to YAML.
+ Added the `IdMap` class.

## Bugfixes

* Fixed an exception when `YamlIdMap` discovers objects of unknown types. #15

## Improvements

* `YamlIdMap` no longer appends auto-incremented numbers to IDs if not necessary. #16

## General

* Transitioned many APIs from concrete implementation types like `ArrayList` or `LinkedHashMap` to their respective 
  interfaces like `List` or `Map`.
  > In places where this could not be done in-place, new APIs were added and the old ones deprecated.
  > See the respective Javadocs for migration info.
* Deprecated some misplaced or accidentally public APIs.
  > Check for deprecation warnings and see the respective Javadocs for migration info.
* General code cleanup and minor optimizations.

# fulibYaml v1.2.1

## Bugfixes

* The `ReflectorMap.discoverObjects` methods now use breadth-first search.
  > This restores the previous order in which `YamlIdMap`s were serialized.
