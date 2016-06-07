package imf.processor;

import java.awt.event.KeyEvent;

import imf.utility.Pair;
import loot.InputManager;
import loot.InputManager.ButtonState;

public class Keyboard implements IProcess<Integer, Integer>
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
		ACTION(KeyEvent.VK_E),
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

	public static final int STATE_PRESS = 1, STATE_CHANGE = 2;
	
	public Keyboard(InputManager inputs)
	{
		this.inputs = inputs;
	}
	
	@Override
	public void initilize(@SuppressWarnings("rawtypes") IProcess manager) 
	{
		this.manager = (ProcessManager) manager;
		inputs.BindKey(KEYBOARD.UP.getKey(), KEYBOARD.UP.ordinal());
		inputs.BindKey(KEYBOARD.DOWN.getKey(), KEYBOARD.DOWN.ordinal());
		inputs.BindKey(KEYBOARD.LEFT.getKey(), KEYBOARD.LEFT.ordinal());
		inputs.BindKey(KEYBOARD.RIGHT.getKey(), KEYBOARD.RIGHT.ordinal());
		inputs.BindKey(KEYBOARD.JUMP.getKey(), KEYBOARD.JUMP.ordinal());	
		inputs.BindKey(KEYBOARD.ACTION.getKey(), KEYBOARD.ACTION.ordinal());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loop()
	{
		for(ButtonState bs : inputs.buttons)
		{
			if (bs.ID < 0)
				break;
			
			int state = 0;
			state |= bs.isPressed ? STATE_PRESS : 0;
			state |= bs.isChanged ? STATE_CHANGE: 0;

			if(state == Keyboard.STATE_CHANGE + Keyboard.STATE_PRESS)
				if(bs.ID == KEYBOARD.ACTION.ordinal())
					manager.get("interaction").setter(new Pair<String> ("act", (String) manager.get("physics").getter()));
			
			if(state == Keyboard.STATE_PRESS)
				manager.get("physics").setter(bs.ID);
		}
	}
	
	@Override
	public void process() 
	{
		
	}
	
	@Override
	public void finalize() 
	{
		
	}

	@Override
	public void setter(Integer object) 
	{
		
	}

	@Override
	public Integer getter() 
	{
		return null;
	}
}
