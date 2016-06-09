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
		HELP(KeyEvent.VK_F1),
		EMOTION1(KeyEvent.VK_1),
		EMOTION2(KeyEvent.VK_2),
		EMOTION3(KeyEvent.VK_3),
		EMOTION4(KeyEvent.VK_4),
		EMOTION5(KeyEvent.VK_5),
		EMOTION6(KeyEvent.VK_6),
		EMOTION7(KeyEvent.VK_7),
		EMOTION8(KeyEvent.VK_8),
		EMOTION9(KeyEvent.VK_9),
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
		inputs.BindKey(KEYBOARD.HELP.getKey(), KEYBOARD.HELP.ordinal());
		inputs.BindKey(KEYBOARD.EMOTION1.getKey(), KEYBOARD.EMOTION1.ordinal());
		inputs.BindKey(KEYBOARD.EMOTION2.getKey(), KEYBOARD.EMOTION2.ordinal());
		inputs.BindKey(KEYBOARD.EMOTION3.getKey(), KEYBOARD.EMOTION3.ordinal());
		inputs.BindKey(KEYBOARD.EMOTION4.getKey(), KEYBOARD.EMOTION4.ordinal());
		inputs.BindKey(KEYBOARD.EMOTION5.getKey(), KEYBOARD.EMOTION5.ordinal());
		inputs.BindKey(KEYBOARD.EMOTION6.getKey(), KEYBOARD.EMOTION6.ordinal());
		inputs.BindKey(KEYBOARD.EMOTION7.getKey(), KEYBOARD.EMOTION7.ordinal());
		inputs.BindKey(KEYBOARD.EMOTION8.getKey(), KEYBOARD.EMOTION8.ordinal());
		inputs.BindKey(KEYBOARD.EMOTION9.getKey(), KEYBOARD.EMOTION9.ordinal());
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
			{
				if(bs.ID == KEYBOARD.ACTION.ordinal())
					manager.get("interaction").setter(new Pair<String> ("act", (String) manager.get("physics").getter()));
				if(bs.ID == KEYBOARD.HELP.ordinal())
					manager.get("interaction").setter(new Pair<String> ("act_child", "help"));
				if(bs.ID >= KEYBOARD.EMOTION1.ordinal()  && bs.ID <= KEYBOARD.EMOTION9.ordinal())
					manager.get("interaction").setter(new Pair<String> ("act_emotion", "emotion@emo_" + (bs.ID - 6)));
			}
			
			if(state >= Keyboard.STATE_PRESS)		
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
		manager = null;
		inputs = null;
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
