package imf.processor;

import java.awt.Canvas;
import java.awt.event.KeyEvent;

import loot.InputManager;
import loot.InputManager.ButtonState;

public class Keyboard implements IProcess
{
	ProcessManager manager;
	InputManager inputs;
	
	public enum KEYBOARD
	{ 
		UP(KeyEvent.VK_W),
		DOWN(KeyEvent.VK_S),
		LEFT(KeyEvent.VK_A),
		RIGHT(KeyEvent.VK_D),
		JUMP(KeyEvent.VK_SPACE),
		STOP(KeyEvent.VK_E),
		/**
		 * FINAL, count enum elements;
		 */
		FINAL(KeyEvent.CHAR_UNDEFINED);
		
		private int key;
		
		KEYBOARD(int key) {
			this.key = key;
		}

		public int getKey() {
			return this.key;
		}
	}

	static final int STATE_PRESS = 1, STATE_CHANGE = 2;
	
	public Keyboard(InputManager inputs, ProcessManager manager)
	{
		this.inputs = inputs;
		this.manager = manager;
	}
	
	@Override
	public void initilize() 
	{
		inputs.BindKey(KEYBOARD.UP.getKey(), KEYBOARD.UP.ordinal());
		inputs.BindKey(KEYBOARD.DOWN.getKey(), KEYBOARD.DOWN.ordinal());
		inputs.BindKey(KEYBOARD.LEFT.getKey(), KEYBOARD.LEFT.ordinal());
		inputs.BindKey(KEYBOARD.RIGHT.getKey(), KEYBOARD.RIGHT.ordinal());
		inputs.BindKey(KEYBOARD.JUMP.getKey(), KEYBOARD.JUMP.ordinal());	
		inputs.BindKey(KEYBOARD.STOP.getKey(), KEYBOARD.STOP.ordinal());
	}

	@Override
	public void process() 
	{
		inputs.AcceptInputs();
		for(ButtonState bs : inputs.buttons)
		{
			if (bs.ID < 0)
				break;
			
			int state = 0;
			state |= bs.isPressed ? STATE_PRESS : 0;
			state |= bs.isChanged ? STATE_CHANGE: 0;
			
			if(state == 1)
				manager.get("physics").utility(bs.ID);
		}
	}
	
	@Override
	public void finalize() 
	{
		
	}

	@Override
	public void utility(Integer arg) 
	{
		
	}
	
}
