# Legacy Fabric Mod API

A re-implementation of Hypixel's [Forge Mod API](https://github.com/HypixelDev/ForgeModAPI) for Legacy Fabric, 1.8.9.

## Notice

Please note that `LegacyFabricModAPI` does not hold a 1-to-1 API with `ForgeModAPI`.

```
field onHypixel -> connectedToHypixel
method setOnHypixel -> setConnectedToHypixel
```

However it's extremely unlikely you'll be hindered by this - it implements `HypixelModAPIImplementation` which you will most likely be working with.
