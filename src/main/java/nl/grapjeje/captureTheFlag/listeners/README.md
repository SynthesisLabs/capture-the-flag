# Listener Framework

Deze map bevat alle event listeners voor de CaptureTheFlag-plugin. Dit document legt uit hoe je een nieuwe listener maakt en registreert.

---

## 1. Een nieuwe Listener aanmaken

1. Maak een nieuwe Java-klasse in de `listeners` map.
2. Laat de klasse het Bukkit `Listener`-interface implementeren.
3. Voeg event-methodes toe met `@EventHandler`.

### Voorbeeld:

```java
public class TestListener implements Listener {

    @EventHandler
    public void onTest(TestEvent e) {
        // Voeg hier je event-logica toe
    }
}
```

Je kan ook meerdere Eventhandlers in een klasse hebben

```java
public class TestListener implements Listener {

    @EventHandler
    public void onTest(TestEvent e) {
        // Voeg hier je event-logica toe
    }

    @EventHandler
    public void onTest2(AnotherTestEvent e) { // Zorg voor een andere naam!
        // Voeg hier je event-logica toe
    }
}
```

In een Listener-klasse schrijf je normale Java-code, maar alle functionaliteit wordt uitgevoerd vanuit methodes met @EventHandler. Zie deze methode als het startpunt van de listener.

### Voorbeeld:

```java
public class TestListener implements Listener {
    
    private boolean isOnline = false;

    @EventHandler
    public void onTest(TestEvent e) {
        Player player = e.getPlayer;
        this.isOnline = this.isOnline(player);
    }

    public boolean isOnline(Player player) {
        return player.isOnline();
    }
}
```

## 2 Listener registreren

1. Ga opzoek naar de ```ListenerManager.java``` (Je kunt een instantie ervan vinden in ```Main.java```)
2. Voeg daar jouw aangemaakte Listener toe!

### Voorbeeld:

```java
public void init() {
    this.registerEventListener(new TestListener()); // Jouw eigen listener
}
```
