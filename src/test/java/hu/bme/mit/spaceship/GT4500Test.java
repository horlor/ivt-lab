package hu.bme.mit.spaceship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Any;

import static org.mockito.Mockito.*;

public class GT4500Test {

  private GT4500 ship;
  private ITorpedoStore mockPrimaryStore ;
  private ITorpedoStore mockSecondaryStore ;

  @BeforeEach
  public void init(){
    mockPrimaryStore = mock(ITorpedoStore.class);
    mockSecondaryStore = mock(ITorpedoStore.class);
    this.ship = new GT4500(mockPrimaryStore, mockSecondaryStore);
  }

  @Test
  public void fireTorpedo_Single_Success(){
    // Arrange
    when(mockPrimaryStore.fire(1)).thenReturn(true);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(1);
    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertTrue(result);
    verify(mockPrimaryStore,times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_All_Success(){
    // Arrange
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(3);
    when(mockPrimaryStore.fire(3)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, atLeast(1)).fire(3);
  }

  @Test
  public void fireTorpedo_Single_Primary_Success(){
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(1);
    when(mockPrimaryStore.fire(1)).thenReturn(true);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenReturn(false);

    ship.fireTorpedo(FiringMode.SINGLE);

    verify(mockPrimaryStore,times(1)).fire(1);
    verify(mockSecondaryStore,times(0)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_BothEmpty(){
    when(mockPrimaryStore.isEmpty()).thenReturn(true);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(1);
    when(mockPrimaryStore.fire(1)).thenReturn(true);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenReturn(false);
    
    ship.fireTorpedo(FiringMode.SINGLE);

    verify(mockPrimaryStore,never()).fire(anyInt());
    verify(mockSecondaryStore,never()).fire(anyInt());
  }

  @Test
  public void fireTorpedo_Single_Secondary_Success(){
    when(mockPrimaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.getTorpedoCount()).thenReturn(1);
    when(mockPrimaryStore.fire(1)).thenReturn(false);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

    ship.fireTorpedo(FiringMode.SINGLE);

    verify(mockPrimaryStore,times(0)).fire(1);
    verify(mockSecondaryStore,times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_Primary_Failure(){
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(1);
    when(mockPrimaryStore.fire(1)).thenReturn(false);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.fire(1)).thenReturn(false);

    boolean result = ship.fireTorpedo(FiringMode.SINGLE);
    
    assertFalse(result);
    verify(mockPrimaryStore,times(1)).fire(1);
    verify(mockSecondaryStore,times(0)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_All_Empty(){
    when(mockPrimaryStore.isEmpty()).thenReturn(true);
    when(mockPrimaryStore.fire(1)).thenReturn(false);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenReturn(false);

    ship.fireTorpedo(FiringMode.SINGLE);

    verify(mockPrimaryStore,times(0)).fire(1);
    verify(mockSecondaryStore,times(0)).fire(1);
  }

  @Test
  public void fireTorpedo_All_Only_Primary_Success(){
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(5);
    when(mockPrimaryStore.fire(5)).thenReturn(true);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.fire(anyInt())).thenReturn(false);

    ship.fireTorpedo(FiringMode.ALL);

    verify(mockPrimaryStore,atLeast(1)).fire(anyInt());
    verify(mockSecondaryStore,times(0)).fire(anyInt());
  }

  @Test
  public void fireTorpedo_All_Only_Secondary_Success(){
    when(mockPrimaryStore.isEmpty()).thenReturn(true);
    when(mockPrimaryStore.fire(anyInt())).thenReturn(true);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.getTorpedoCount()).thenReturn(3);
    when(mockSecondaryStore.fire(3)).thenReturn(true);

    ship.fireTorpedo(FiringMode.ALL);

    verify(mockPrimaryStore,never()).fire(anyInt());
    verify(mockSecondaryStore,atLeast(1)).fire(anyInt());
  }

  @Test
  public void fireTorpedo_Single_Twice_Alternating_Success(){
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(anyInt())).thenReturn(true);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(5);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.getTorpedoCount()).thenReturn(3);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

    ship.fireTorpedo(FiringMode.SINGLE);

    verify(mockPrimaryStore,times(1)).fire(anyInt());
    verify(mockSecondaryStore,never()).fire(anyInt());

    ship.fireTorpedo(FiringMode.SINGLE);

    verify(mockSecondaryStore,times(1)).fire(anyInt());

  }

  @Test
  public void fireTorpedo_Single_Twice_Alternating_OnlyPrimary(){
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(anyInt())).thenReturn(true);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(5);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.getTorpedoCount()).thenReturn(0);
    when(mockSecondaryStore.fire(1)).thenReturn(false);

    ship.fireTorpedo(FiringMode.SINGLE);

    verify(mockPrimaryStore,times(1)).fire(anyInt());
    verify(mockSecondaryStore,never()).fire(anyInt());

    ship.fireTorpedo(FiringMode.SINGLE);

    verify(mockPrimaryStore,times(2)).fire(anyInt());
    verify(mockSecondaryStore,never()).fire(anyInt());

  }

  @Test
  public void fireTorpedo_Single_Twice_Alternating_OnlyPrimary_SecondFails(){
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(anyInt())).thenReturn(true);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(1);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.getTorpedoCount()).thenReturn(0);
    when(mockSecondaryStore.fire(1)).thenReturn(false);

    ship.fireTorpedo(FiringMode.SINGLE);

    verify(mockPrimaryStore,times(1)).fire(anyInt());
    verify(mockSecondaryStore,never()).fire(anyInt());

    when(mockPrimaryStore.isEmpty()).thenReturn(true);

    ship.fireTorpedo(FiringMode.SINGLE);

    verify(mockPrimaryStore,times(1)).fire(anyInt());
    verify(mockSecondaryStore,never()).fire(anyInt());

  }
  
  @Test
  public void fireTorpedo_All_Success_Primary(){
    // Arrange
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(anyInt())).thenReturn(true);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(5);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.getTorpedoCount()).thenReturn(3);
    when(mockSecondaryStore.fire(anyInt())).thenReturn(false);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, atLeast(1)).fire(anyInt());
    verify(mockSecondaryStore, atLeast(1)).fire(anyInt());
  }

  @Test
  public void fireTorpedo_All_Success_Secondary(){
    // Arrange
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(anyInt())).thenReturn(false);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(3);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.getTorpedoCount()).thenReturn(5);
    when(mockSecondaryStore.fire(anyInt())).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, atLeast(1)).fire(anyInt());
    verify(mockSecondaryStore, atLeast(1)).fire(anyInt());
  }

  @Test
  public void fireTorpedo_All_Success_Both(){
    // Arrange
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(anyInt())).thenReturn(true);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(3);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.getTorpedoCount()).thenReturn(5);
    when(mockSecondaryStore.fire(anyInt())).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryStore, atLeast(1)).fire(anyInt());
    verify(mockSecondaryStore, atLeast(1)).fire(anyInt());
  }

  public void fireTorpedo_All_Empty(){
    // Arrange
    when(mockPrimaryStore.isEmpty()).thenReturn(true);
    when(mockPrimaryStore.fire(anyInt())).thenReturn(false);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(3);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.getTorpedoCount()).thenReturn(5);
    when(mockSecondaryStore.fire(anyInt())).thenReturn(false);

    // Act
    ship.fireTorpedo(FiringMode.ALL);

    // Assert
    verify(mockPrimaryStore, never()).fire(anyInt());
    verify(mockSecondaryStore, never()).fire(anyInt());
  }

  public void fireTorpedo_All_Failure(){
    // Arrange
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(anyInt())).thenReturn(false);
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(3);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.getTorpedoCount()).thenReturn(5);
    when(mockSecondaryStore.fire(anyInt())).thenReturn(false);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertFalse(result);
    verify(mockPrimaryStore, atLeast(1)).fire(anyInt());
    verify(mockSecondaryStore, atLeast(1)).fire(anyInt());
  }





}
