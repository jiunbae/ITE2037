package loot;

import java.io.File;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * 프로그램에서 사용할 음원들을 읽어오고 제어 / 관리할 수 있는 클래스입니다.<br>
 * <br>
 * <b>주의:</b><br>
 * 이 클래스는 모든 소리 파일 형식을 지원하지 않으며<br>
 * 심지어 같은 형식이라도 내부 설정에 따라 지원 여부가 달라지기도 합니다.<br>
 * 여러분의 음원 파일이 제대로 등록되지 않는 경우<br>
 * 반드시 조교에게 도움을 요청하세요.
 * 
 * @author Racin
 *
 */
public class AudioManager
{
	HashMap<String, Clip[]> clips;
	
	public AudioManager()
	{
		clips = new HashMap<>();
	}
	
	/**
	 * 해당 파일에서 음원을 읽어와 주어진 이름으로 등록합니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * AudioManager class는 모든 소리 파일 형식을 지원하지 않으며<br>
	 * 심지어 같은 형식이라도 내부 설정에 따라 지원 여부가 달라지기도 합니다.<br>
	 * 여러분의 음원 파일이 제대로 등록되지 않는 경우<br>
	 * 반드시 조교에게 도움을 요청하세요.
	 * 
	 * @param fileName 음원을 읽어올 파일 이름입니다.
	 * @param clipName 프로그램 내에서 사용하기 위해 부여하는 이 음원의 이름입니다.
	 * @param numberOfChannels 해당 음원의 채널 수('동시에' 얼마나 많이 재생할 수 있는지)를 지정합니다. 
	 * @return 성공적으로 등록한 경우 true를 반환합니다.
	 */
	public boolean LoadAudio(String fileName, String clipName, int numberOfChannels)
	{
		try
		{
			//이미 해당 이름의 음원이 등록되어 있는 경우 실패
			if ( clips.containsKey(clipName) == true )
			{
				return false;
			}
			
			File f = new File(fileName);
			
			//해당 이름의 파일이 없는 경우 실패
			if ( f.exists() == false )
			{
				return false;
			}
			
			Clip[] channels = new Clip[numberOfChannels];
			
			for ( int iChannel = 0; iChannel < numberOfChannels; ++iChannel )
			{
				Clip clip = AudioSystem.getClip();
				AudioInputStream as = AudioSystem.getAudioInputStream(f);
				clip.open(as);
				channels[iChannel] = clip;
			}
			
			clips.put(clipName, channels);
		}
		catch ( Exception e )
		{
			//음원을 읽어오지 못 한 경우 실패
			e.printStackTrace();
			return false;			
		}
		
		return true;
	}
	
	
	/**
	 * 해당 이름으로 등록된 음원을 재생합니다.<br>
	 * 해당 음원이 가지고 있는 채널이 모두 사용중인 경우 가장 오래된 채널을 멈추고 다시 재생을 시작합니다.
	 * 
	 * @param clipName 등록할 때 부여한 음원의 이름입니다.
	 */
	public void Play(String clipName)
	{
		//해당 이름으로 등록된 음원이 없는 경우 실패
		if ( clips.containsKey(clipName) == false )
			return;
		
		Clip[] channels = clips.get(clipName);
		
		//사용하고 있지 않은 채널이 있는 경우 사용
		for ( Clip clip : channels )
		{
			if ( clip.isRunning() == false )
			{
				clip.setFramePosition(0);
				clip.start();
				return;
			}
		}
		
		//모든 채널이 사용중인 경우 가장 오래된 채널을 멈추고 다시 재생 시작
		int max_framePosition = -1;
		Clip clip_max_framePosition = null;
		
		for ( Clip clip : channels )
		{
			if ( clip.getFramePosition() > max_framePosition )
			{
				clip_max_framePosition = clip;
				max_framePosition = clip.getFramePosition();
			}
		}
		
		clip_max_framePosition.stop();
		clip_max_framePosition.flush();
		clip_max_framePosition.setFramePosition(0);
		clip_max_framePosition.start();
	}
	
	/**
	 * 해당 이름으로 등록된 음원을 반복 재생합니다.<br>
	 * 해당 음원이 가지고 있는 채널이 모두 사용중인 경우 가장 오래된 채널을 멈추고 다시 재생을 시작합니다.
	 * 
	 * @param clipName 등록할 때 부여한 음원의 이름입니다.
	 * @param count
	 * 			반복할 횟수를 지정합니다.<br>
	 * 			이 값이 1이면 '한 번 반복'을 수행하여 '두 번 재생'하게 됩니다.<br>
	 * 			이 값이 음수면 '무한 반복'을 수행합니다.
	 */
	public void Loop(String clipName, int count)
	{
		//해당 이름으로 등록된 음원이 없는 경우 실패
		if ( clips.containsKey(clipName) == false )
			return;
		
		//반복 횟수가 음수인 경우 '무한 반복'으로 간주
		if ( count < 0 )
			count = Clip.LOOP_CONTINUOUSLY;
		
		Clip[] channels = clips.get(clipName);
		
		//사용하고 있지 않은 채널이 있는 경우 사용
		for ( Clip clip : channels )
		{
			if ( clip.isRunning() == false )
			{
				clip.setFramePosition(0);
				clip.loop(count);
				return;
			}
			
			clip.loop(0);
			
		}
		
		//모든 채널이 사용중인 경우 가장 오래된 채널을 멈추고 다시 재생 시작
		int max_framePosition = -1;
		Clip clip_max_framePosition = null;
		
		for ( Clip clip : channels )
		{
			if ( clip.getFramePosition() > max_framePosition )
			{
				clip_max_framePosition = clip;
				max_framePosition = clip.getFramePosition();
			}
		}
		
		clip_max_framePosition.stop();
		clip_max_framePosition.flush();
		clip_max_framePosition.setFramePosition(0);
		clip_max_framePosition.loop(count);
	}
	
	/**
	 * 해당 이름으로 등록된 음원의 재생을 모두 중단합니다.<br>
	 * 해당 음원이 여러 채널을 가지고 있는 경우 모든 채널의 재생이 중단됩니다.
	 * 
	 * @param clipName 등록할 때 부여한 음원의 이름입니다.
	 */
	public void Stop(String clipName)
	{
		//해당 이름으로 등록된 음원이 없는 경우 실패
		if ( clips.containsKey(clipName) == false )
			return;
		
		Clip[] channels = clips.get(clipName);
		
		//모든 채널 정지
		for ( Clip clip : channels )
		{
			clip.stop();
			clip.flush();
		}
	}
	
	/**
	 * 해당 이름으로 등록된 음원의 길이를 가져옵니다. 단위는 ms입니다.
	 * 
	 * @param clipName 등록할 때 부여한 음원의 이름입니다.
	 */
	public long GetLength(String clipName)
	{
		//해당 이름으로 등록된 음원이 없는 경우 실패
		if ( clips.containsKey(clipName) == false )
			return -1;
		
		Clip[] channels = clips.get(clipName);
		
		return channels[0].getMicrosecondLength() / 1000L;
	}
}
