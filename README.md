# 🚧 WORK-IN-PROGRESS 

Create 2D games with simple Java code for any desktop platform. 

## Getting Started

The code below creates a window and sets its color to white.

```java
import org.chisel2d.ChiselApp;
import org.chisel2d.util.Color; 

// Extend the ChiselApp class, providing the application entry point. 
public class MyApp extends ChiselApp {

  public static void main(String[] args) {
    // Create a new application titled 'Hello, world!' and sized 500x500. 
    // Control is returned here when the application exits. 
    new MyApp().launch("Hello, world!", 500, 500); 
  }
  
  // Initialisation -> set the background color.
  @Override 
  protected void setup() {
    setBackgroundColor(Color.WHITE); 
  }
  
  @Override 
  protected void onTick() {
    // Called on each game tick (update)
  }
}
```
