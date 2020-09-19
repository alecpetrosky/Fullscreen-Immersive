# Android-Immerse-Demo

## Solved Challenges

- [A strange artifact in full screen on some devices](https://stackoverflow.com/questions/63924072/android-a-strange-artifact-in-full-screen-on-some-devices) solved with the help of [Javad Dehban](https://stackoverflow.com/a/63924328/13776879).

## Rejected Code

### Getting navigation bar height

This code below works great except some fancy devices such as Motorola Moto G8 where it gives wrong value (199 instead of 126).

```java
Point appUsableSize = getAppUsableScreenSize(context);
Point realScreenSize = getRealScreenSize(context);
int navigationBarHeight = 0;

// navigation bar at the bottom
if (appUsableSize.y < realScreenSize.y) {
    navigationBarHeight = realScreenSize.y - appUsableSize.y;
}
```


## Credits

- Photo **Llama on Machu Picchu** By Alexandre Buisse (Nattfodd) - Own work (http://www.alexandrebuisse.org), CC BY-SA 3.0, https://commons.wikimedia.org/w/index.php?curid=2841095
- Icons by [Ben Sperry](https://github.com/ionic-team/ionicons), MIT, https://github.com/ionic-team/ionicons/blob/master/LICENSE

