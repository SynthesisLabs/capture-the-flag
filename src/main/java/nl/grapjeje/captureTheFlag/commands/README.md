# Command Framework

Deze map bevat alle commands voor de CaptureTheFlag-plugin. Dit document legt uit hoe je een nieuwe command maakt en registreert.

---

## 1. Een nieuwe Command aanmaken

1. Maak een nieuwe Java-klasse in de `commands` map.
2. Maak een klasse die de custom Command klasse uitbreidt.
3. Implementeer de methode(s).
4. Maak de constructor met de command naam.

### Zo moet het er nu uit zien:

```java
public class TestCommand extends Command {
    public TestCommand() {
        super("test");
    }

    @Override
    protected void execute(String[] args) {
        
    }
}
```

In de execute zet je alle logica neer voor je command.

```java
@Override
protected void execute(String[] args) {
    this.getPlayer().sendMessage("Hello World!");
}
```

In de ```String[] args``` parameter zitten alle argumenten die de speler heeft meegegeven.

In het commando ```/test a b c``` kun je de eerste parameter krijgen met ```args[0]```. De rest spreekt voor zich.

#### In een Command-klasse schrijf je normale Java-code, maar alle functionaliteit wordt uitgevoerd vanuit de execute methode. Zie deze methode als het startpunt van het command.

### Voorbeeld:

```java
public class TestCommand extends Command {

    public TestCommand() {
        super("test");
    }

    private boolean isOnline = false;

    @Override
    protected void execute(String[] args) {
        this.isOnline = this.isOnline(this.getPlayer());
        // this.getPlayer() returned null als dit commando niet door een speler is verstuurd. 
        // Bijvoorbeeld door de console.
    }

    public boolean isOnline(Player player) {
        if (player == null) return false;
        return player.isOnline();
    }
}
```

## 2 Commands registreren

1. Ga opzoek naar de ```CommandManager.java``` (Je kunt een instantie ervan vinden in ```Main.java```)
2. Voeg daar jouw aangemaakte Command toe!

### Voorbeeld:

```java
public void init() {
    this.registerCommand(new TestCommand()); // Jou eigen command
}
```

We hebben hem nu in onze plugin geregristeerd, maar Minecraft zelf kent het commando nog niet. Voeg het command toe in de ```plugin.yml``` in je resources folder.

#### De naam moet overeen komen met de naam die je op hebt gegeven in je klasse. Anders werkt hij NIET.
```yml
commands:
  test:
    description: This is a test command
```

Bij elk commando voeg je minimaal een description toe dat je weet waarvoor het commando is.

Bekijk hier wat er allemaal mogelijk is: [Paper Docs](https://docs.papermc.io/paper/dev/plugin-yml/#commands)
