/*********************************************************************
	Copyright (c) 2012 by iPanel Technologies, Ltd.
	All rights reserved. You are not allowed to copy or distribute
	the code without permission.
*********************************************************************/
#include "iplayer_ts_parse.h"

/************************************************************************************************
 *宏定义
 ************************************************************************************************/

#define TS_PARSE_MARK "[IVOD][TS_IPARSE]"

/************************************************************************************************
 *结构体定义
 ************************************************************************************************/


/************************************************************************************************
 *函数声明
 ************************************************************************************************/

static int 
ts_iparse_pat_info(unsigned char *buffer);

static int 
ts_iparse_pmt_info(unsigned char *buffer,ts_stream_info *info);

static unsigned int
ts_iparse_getTimeStamp(unsigned char *buffer);

static int 
ts_iparse_get_codec_string(int codec, unsigned char *buf);

static int 
ts_iparse_get_langcode(char *letter);



static char *s_lettercode[] ={                                                         
                                "abk","ace","ach","ada","aar",                                 
                                "afh","afr","afa","aka","akk",                                 
                                "alb","sqi","ale","alg","tut",                                 
                                "amh","apa","ara","arc","arp",                                 
                                "arn","arw","arm","hye","art",                                 
                                "asm","ath","map","ava","ave",                                 
                                "awa","aym","aze","nah","ban",                                 
                                "bat","bal","bam","bai","bad",                                 
                                "bnt","bas","bak","baq","eus",                                 
                                "bej","bem","ben","ber","bho",                                 
                                "bih","bik","bin","bis","bra",                                 
                                "bre","bug","bul","bua","bur",                                 
                                "mya","bel","cad","car","cat",                                 
                                "cau","ceb","cel","cai","chg",                                 
                                "cha","che","chr","chy","chb",                                 
                                "chi","zho","chn","cho","chu",                                 
                                "chv","cop","cor","cos","cre",                                 
                                "mus","crp","cpe","cpf","cpp",                                 
                                "cus","ces","cze","dak","dan",                                 
                                "del","din","div","doi","dra",                                 
                                "dua","dut","nla","dum","dyu",                                 
                                "dzo","efi","egy","eka","elx",                                 
                                "eng","enm","ang","esk","epo",                                 
                                "est","ewe","ewo","fan","fat",                                 
                                "fao","fij","fin","fiu","fon",                                 
                                "fra","fre","frm","fro","fry",                                 
                                "ful","gaa","gae","gdh","glg",                                 
                                "lug","gay","gez","geo","kat",                                 
                                "deu","ger","gmh","goh","gem",                                 
                                "gil","gon","got","grb","grc",                                 
                                "ell","gre","kal","grn","guj",                                 
                                "hai","hau","haw","heb","her",                                 
                                "hil","him","hin","hmo","hun",                                 
                                "hup","iba","ice","isl","ibo",                                 
                                "ijo","ilo","inc","ine","ind",                                 
                                "ina","ine","iku","ipk","ira",                                 
                                "gai","iri","sga","mga","iro",                                 
                                "ita","jpn","jav","jaw","jrb",                                 
                                "jpr","kab","kac","kam","kan",                                 
                                "kau","kaa","kar","kas","kaw",                                 
                                "kaz","kha","khm","khi","kho",                                 
                                "kik","kin","kir","kom","kon",                                 
                                "kok","kor","kpe","kro","kua",                                 
                                "kum","kur","kru","kus","kut",                                 
                                "lad","lah","lam","oci","lao",                                 
                                "lat","lav","ltz","lez","lin",                                 
                                "lit","loz","lub","lui","lun",                                 
                                "luo","mac","mak","mad","mag",                                 
                                "mai","mak","mlg","may","msa",                                 
                                "mal","mlt","man","mni","mno",                                 
                                "max","mao","mri","mar","chm",                                 
                                "mah","mwr","mas","myn","men",                                 
                                "mic","min","mis","moh","mol",                                 
                                "mkh","lol","mon","mos","mul",                                 
                                "mun","nau","nav","nde","nbl",                                 
                                "ndo","nep","new","nic","ssa",                                 
                                "niu","non","nai","nor","nno",                                 
                                "nub","nym","nya","nyn","nyo",                                 
                                "nzi","oji","ori","orm","osa",                                 
                                "oss","oto","pal","pau","pli",                                 
                                "pam","pag","pan","pap","paa",                                 
                                "fas","per","peo","phn","pol",                                 
                                "pon","por","pra","pro","pus",                                 
                                "que","roh","raj","rar","roa",                                 
                                "ron","rum","rom","run","rus",                                 
                                "sal","sam","smi","smo","sad",                                 
                                "sag","san","srd","sco","sel",                                 
                                "sem","scr","srr","shn","sna",                                 
                                "sid","bla","snd","sin","sit",                                 
                                "sio","sla","ssw","slk","slo",                                 
                                "slv","sog","som","son","wen",                                 
                                "nso","sot","sai","esl","spa",                                 
                                "suk","sux","sun","sus","swa",                                 
                                "ssw","sve","swe","syr","tgl",                                 
                                "tah","tgk","tmh","tam","tat",                                 
                                "tel","ter","tha","bod","tib",                                 
                                "tig","tir","tem","tiv","tli",                                 
                                "tog","ton","tru","tsi","tso",                                 
                                "tsn","tum","tur","ota","tuk",                                 
                                "tyv","twi","uga","uig","ukr",                                 
                                "umb","und","urd","uzb","vai",                                 
                                "ven","vie","vol","vot","wak",                                 
                                "wal","war","was","cym","wel",                                 
                                "wol","xho","sah","yao","yap",                                 
                                "yid","yor","zap","zen","zha","zul"};                          
static char *s_fullcode[] ={                                         
                               "Abkhazian","Achinese","Acoli","Adangme","Afar",            
                               "Afrihili","Afrikaans","Afro-Asiatic","Akan",               
                               "Akkadian","Albanian","Albanian","Aleut","Algonquian",      
                               "Altaic","Amharic","Apache","Arabic","Aramaic",             
                               "Arapaho","Araucanian","Arawak","Armenian","Armenian",      
                               "Artificial","Assamese","Athapascan","Austronesian",        
                               "Avaric","Avestan","Awadhi","Aymara","Azerbaijani",         
                               "Aztec","Balinese","Baltic","Baluchi","Bambara",            
                               "Bamileke","Banda","Bantu","Basa","Bashkir","Basque",       
                               "Basque","Beja","Bemba","Bengali","Berber","Bhojpuri",      
                               "Bihari","Bikol","Bini","Bislama","Braj","Breton",          
                               "Buginese","Bulgarian","Buriat","Burmese","Burmese",        
                               "Byelorussian","Caddo","Carib","Catalan","Caucasian",       
                               "Cebuano","Celtic","Central-American(Indian)","Chagatai",   
                               "Chamorro","Chechen","Cherokee","Cheyenne","Chibcha",       
                               "Chinese","Chinese","Chinook","Choctaw","Church","Chuvash", 
                               "Coptic","Cornish","Corsican","Cree","Creek",               
                               "Creoles(Pidgins)","Creoles(Pidgins)","Creoles(Pidgins)",   
                               "Creoles(Pidgins)","Cushitic","Czech","Czech","Dakota",     
                               "Danish","Delaware","Dinka","Divehi","Dogri","Dravidian",   
                               "Duala","Dutch","Dutch","Dutch-Middle","Dyula","Dzongkha",  
                               "Efik","Egyptian","Ekajuk","Elamite","English",             
                               "English-Middle","English-Old","Eskimo","Esperanto",        
                               "Estonian","Ewe","Ewondo","Fang","Fanti","Faroese",         
                               "Fijian","Finnish","Finno-Ugrian","Fon","French",           
                               "French","French-Middle","French-Old","Frisian",            
                               "Fulah","Ga","Gaelic","Gaelic","Gallegan","Ganda",          
                               "Gayo","Geez","Georgian","Georgian","German","German",      
                               "German-Middle","German-Old","Germanic","Gilbertese",       
                               "Gondi","Gothic","Grebo","Greek-Ancient","Greek",           
                               "Greek","Greenlandic","Guarani","Gujarati","Haida",         
                               "Hausa","Hawaiian","Hebrew","Herero","Hiligaynon",          
                               "Himachali","Hindi","Hiri","Hungarian","Hupa","Iban",       
                               "Icelandic","Icelandic","Igbo","Ijo","Iloko","Indic",       
                               "Indo-European","Indonesian","Interlingua","Interlingue",   
                               "Inuktitut","Inupiak","Iranian","Irish","Irish",            
                               "Irish-Old","Irish-Middle","Iroquoian","Italian",           
                               "Japanese","Javanese","Javanese","Judeo-Arabic",            
                               "Judeo-Persian","Kabyle","Kachin","Kamba","Kannada",        
                               "Kanuri","Kara-Kalpak","Karen","Kashmiri","Kawi",           
                               "Kazakh","Khasi","Khmer","Khoisan","Khotanese","Kikuyu",    
                               "Kinyarwanda","Kirghiz","Komi","Kongo","Konkani",           
                               "Korean","Kpelle","Kru","Kuanyama","Kumyk","Kurdish",       
                               "Kurukh","Kusaie","Kutenai","Ladino","Lahnda","Lamba",      
                               "Langue","Lao","Latin","Latvian","Letzeburgesch",           
                               "Lezghian","Lingala","Lithuanian","Lozi","Luba-Katanga",    
                               "Luiseno","Lunda","Luo","Macedonian","Macedonian",          
                               "Madurese","Magahi","Maithili","Makasar","Malagasy",        
                               "Malay","Malay","Malayalam","Maltese","Mandingo",           
                               "Manipuri","Manobo","Manx","Maori","Maori","Marathi",       
                               "Mari","Marshall","Marwari","Masai","Mayan","Mende",        
                               "Micmac","Minangkabau","Miscellaneous","Mohawk",            
                               "Moldavian","Mon-Kmer","Mongo","Mongolian","Mossi",         
                               "Multiple","Munda","Nauru","Navajo","Ndebele-North",        
                               "Ndebele-South","Ndongo","Nepali","Newari",                 
                               "Niger-Kordofanian","Nilo-Saharan","Niuean",                
                               "Norse-Old","North-American(Indian)","Norwegian",           
                               "Norwegian","Nubian","Nyamwezi","Nyanja","Nyankole",        
                               "Nyoro","Nzima","Ojibwa","Oriya","Oromo","Osage",           
                               "Ossetic","Otomian","Pahlavi","Palauan","Pali",             
                               "Pampanga","Pangasinan","Panjabi","Papiamento",             
                               "Papuan-Australian","Persian","Persian","Persian-Old",      
                               "Phoenician","Polish","Ponape","Portuguese","Prakrit",      
                               "Provencal-Old","Pushto","Quechua","Rhaeto-Romance",        
                               "Rajasthani","Rarotongan","Romance","Romanian","Romanian",  
                               "Romany","Rundi","Russian","Salishan","Samaritan(Aramaic)", 
                               "Sami","Samoan","Sandawe","Sango","Sanskrit","Sardinian",   
                               "Scots","Selkup","Semitic","Serbo-Croatian","Serer","Shan", 
                               "Shona","Sidamo","Siksika","Sindhi","Singhalese",           
                               "Sino-Tibetan","Siouan","Slavic","Siswant","Slovak",        
                               "Slovak","Slovenian","Sogdian","Somali","Songhai","Sorbian",
                               "Sotho-Northern","Sotho-Southern","South-American(Indian)", 
                               "Spanish","Spanish","Sukuma","Sumerian","Sudanese","Susu",  
                               "Swahili","Swazi","Swedish","Swedish","Syriac","Tagalog",   
                               "Tahitian","Tajik","Tamashek","Tamil","Tatar","Telugu",     
                               "Tereno","Thai","Tibetan","Tibetan","Tigre","Tigrinya",     
                               "Timne","Tivi","Tlingit","Tonga","Tonga(Tonga-Islands)",    
                               "Truk","Tsimshian","Tsonga","Tswana","Tumbuka","Turkish",   
                               "Turkish-Ottoman","Turkmen","Tuvinian","Twi","Ugaritic",    
                               "Uighur","Ukrainian","Umbundu","Undetermined","Urdu",       
                               "Uzbek","Vai","Venda","Vietnamese","Volap?k","Votic",       
                               "Wakashan","Walamo","Waray","Washo","Welsh","Welsh",        
                               "Wolof","Xhosa","Yakut","Yao","Yap","Yiddish","Yoruba",     
                               "Zapotec","Zenaga","Zhuang","Zulu"};                        

/************************************************************************************************
 *函数定义
 ************************************************************************************************/

/*解析ts流音视频信息*/
int ts_iparse_stream_info(ts_stream_info *info,unsigned char *buffer,int len)
{
	int ret = IPANEL_ERR,i = 0;
	ts_stream_info temp_info[1] = { 0 };      	
	int ts_pid = 0x1FFF;

	FAILED_RETURNX( !info || !buffer || len < 188,IPANEL_ERR);
	
	while ( (0x47 != *buffer || buffer[188] != 0x47) && len >= 0)   
	{
		buffer++;
		len--;
	}

	for (i=0; i < len/188; i++)
	{   
        // syn_byte: MPEG-2TS ( 0x47 )              
		if (0x47 != buffer[0])   
		{
			buffer +=188;
			continue;
		}
		
		ts_pid = (buffer[1]&0x1f)<<8 | buffer[2];
		
		if ((info->pmt_pid <= 0) && (TS_IPARSE_PAT_PID == ts_pid) )   
		{
            // receive PAT table 
			info->pmt_pid = ts_iparse_pat_info(buffer);   
			if( info->pmt_pid <= 0)
			{
				INFO("%s[ts_iparse_stream_info]parse PAT table failed!\n",TS_PARSE_MARK);
				return ret;
			}
			else 
			{
                INFO("%s[ts_iparse_stream_info]parse PAT table SUCCESS! pmt_pid = %2X\n",TS_PARSE_MARK,info->pmt_pid);
			}
		}
        
		if ( (info->pmt_pid >0) && (ts_pid == info->pmt_pid))   
		{
            // receive PMT table
			ret = ts_iparse_pmt_info(buffer,temp_info);                        
			if(ret == IPANEL_ERR)	
            {
				INFO("%s[ts_iparse_stream_info]parse PMT table failed!\n",TS_PARSE_MARK);
				return ret;
			} 
            else 
            {
				temp_info->pmt_pid = info->pmt_pid;
				memcpy(info,temp_info,sizeof(ts_stream_info));
				
				INFO("%s[ts_iparse_stream_info]parse PMT table SUCCESS! audio_pid=%d, video_pid=%d, pcr_pid=%d, audio_streamtype=%d, video_streamtype=%d. \n"
					,TS_PARSE_MARK,
					info->avinfo->audinfo[0].audio_pid,
					info->avinfo->vidinfo[0].video_pid,
					info->pcr_pid,
					info->avinfo->audinfo[0].codec_id,
					info->avinfo->vidinfo[0].codec_id);
				
				return ret;
			}
		}
		
		buffer +=188;
	}
	
	return ret;
}



/*解析PAT表，得到媒体的PMT表信息*/
static int ts_iparse_pat_info(unsigned char *buffer)
{
	int   ret = IPANEL_ERR ; 
	int   pmtid = -1; 	
	int   adaplen = 0;
	unsigned char   adap_flag = 0;
	unsigned char  *patptr = buffer;
	int  progNO = 0,section_length = 0;	
	unsigned char   payload_start_indicator = 0;
	int   count = 0, i = 0 ; 
	
	FAILED_RETURNX( !patptr,IPANEL_ERR);
    
	//payload unit start indicator
	payload_start_indicator =  (buffer[1]&0x40); 
	
	adap_flag = (buffer[3]&0x30);
	
	if (adap_flag == 0x10) 
	{
		patptr += 4;    
	}
	else if (adap_flag == 0x30)
	{
		adaplen = (buffer[4] & 0xff) ;
		patptr   += (adaplen + 5);
	}
	else
	{
		INFO("%s[ts_iparse_pat_info]no payload data\n",TS_PARSE_MARK);
		return ret;
	}
	
	if( payload_start_indicator ) 
	{
		patptr++;
	}
	
	section_length = TS_IPARSE_SECTION_LEN(patptr);
	count = ( section_length-12)>>2 ; 
	patptr += 8; 
	
	for(i=0;i<count;i++) 
	{
		progNO = (patptr[0] << 8) | patptr[1];
		if (progNO != 0)  // 去掉NIT 表的PID (0x0000)
		{
			pmtid = ((patptr[2] & 0x1f) << 8) | (patptr[3] & 0xff);
		}
		
		patptr += 4;
	}
	
    return pmtid;        
}



/*解析PMT表，得到音视频的pid及编码格式*/
static int ts_iparse_pmt_info(unsigned char *buffer,ts_stream_info *info)
{
	int  ret = IPANEL_ERR ;
	unsigned char  adapflag = 0;
	int  adaplen = 0;
	unsigned char  *pmtPtr = buffer,*ptr = NULL,*descriptor_ptr = NULL;
	unsigned char  payload_start_indicator = 0,descriptor_tag = 0;
	int  section_length = 0;	
	int  es_info_length = 0; 
	int  program_info_length = 0;
    int  stream_pid = 0,stream_type = 0;
	
	FAILED_RETURNX(!pmtPtr || !info, ret);
    
	//payload unit start indicator
	payload_start_indicator =  (buffer[1]&0x40); 
	adapflag = buffer[3] & 0x30;
	
	if (adapflag == 0x10) 
    {
		pmtPtr += 4;    
	} 
    else if (adapflag == 0x30) 
    {
		adaplen = (buffer[4] & 0xff) ;
		pmtPtr  += (adaplen + 5);
	} 
    else 	
    {
		INFO("%s[ts_iparse_pmt_info]no payload data\n",TS_PARSE_MARK);
		return ret;
	}
	
	if( payload_start_indicator ) {
		pmtPtr++;
	}
	
	if (pmtPtr[0] != TS_IPARSE_PMT_PID)  
    {
		INFO("%s[ts_iparse_pmt_info]it is not pmt table.\n",TS_PARSE_MARK);
		return ret;
	}
	
	ptr = pmtPtr ;
	section_length = TS_IPARSE_SECTION_LEN(pmtPtr);
	info->pcr_pid = ((pmtPtr[8] & 0x1f) << 8) | pmtPtr[9] ;
	program_info_length = ((pmtPtr[10] & 0x0f) << 8) | pmtPtr[11];
	
	pmtPtr  += (program_info_length+12);
	
	while (pmtPtr < ( ptr + (section_length-4) ) )	
    {
		
		if ((pmtPtr[0] == 0x1) || (pmtPtr[0] == 0x2) 
            || (pmtPtr[0] == 0x1b) || (pmtPtr[0] == 0x42)
            || (pmtPtr[0] == 0x10) || (pmtPtr[0] == 0xea)) 
        {    
			stream_pid = ((pmtPtr[1] & 0x1f) << 8) | (pmtPtr[2] & 0xff);		
			stream_type = pmtPtr[0];

        	if(stream_type == 0x1) 
                info->avinfo->vidinfo[0].codec_id = MEDIAPROCESSOR_VIDEO_TYPE_MPEG1;
        	else if(stream_type == 0x2) 
                info->avinfo->vidinfo[0].codec_id = MEDIAPROCESSOR_VIDEO_TYPE_MPEG2;
        	else if(stream_type == 0x1b) 
                info->avinfo->vidinfo[0].codec_id = MEDIAPROCESSOR_VIDEO_TYPE_H264;
        	else if(stream_type == 0x42) 
                info->avinfo->vidinfo[0].codec_id = MEDIAPROCESSOR_VIDEO_TYPE_AVS;
        	else if(stream_type == 0x10) 
                info->avinfo->vidinfo[0].codec_id = MEDIAPROCESSOR_VIDEO_TYPE_MPEG4;
            
            info->avinfo->vidinfo[0].video_pid = stream_pid;
            info->avinfo->vidinfo[0].valid = 1;
            
		}
        else if ((pmtPtr[0] == 0x3) || (pmtPtr[0] == 0x4)
					|| (pmtPtr[0] == 0x0f)|| (pmtPtr[0] == 0x1b) 
					|| (pmtPtr[0] == 0x11) || (pmtPtr[0] == 0x80) 
					|| (pmtPtr[0] == 0x81) || (pmtPtr[0] == 0x82) 
					|| (pmtPtr[0] == 0x83) || (pmtPtr[0] == 0x86)) 
		{	
			stream_pid = ((pmtPtr[1] & 0x1f) << 8) | (pmtPtr[2] & 0xff);		
			stream_type = pmtPtr[0];

            if(info->avinfo->audio_channel_num < MAX_TS_AUDIO_TRACKS_NUM)
            {
                int find_lang = 0;

    		    if(stream_type == 0x3) 
    				info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id= MEDIAPROCESSOR_AUDIO_TYPE_MPEG1;
    			else if(stream_type == 0x4) 
    				info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id = MEDIAPROCESSOR_AUDIO_TYPE_MPEG2;
    			else if(stream_type == 0x0f) 
    				info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id = MEDIAPROCESSOR_AUDIO_TYPE_AAC_1;
    			else if(stream_type == 0x80) 
    				info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id = MEDIAPROCESSOR_AUDIO_TYPE_AAC_2;
    			else if(stream_type == 0x11) 
    				info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id = MEDIAPROCESSOR_AUDIO_TYPE_AAC_3;
                else if(stream_type == 0x1b)
    				info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id = MEDIAPROCESSOR_AUDIO_TYPE_AC3_1;           
    			else if(stream_type == 0x81||stream_type == 0x83) 
    				info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id = MEDIAPROCESSOR_AUDIO_TYPE_AC3_2;
    			else if(stream_type == 0x82||stream_type == 0x86)
    				info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id = MEDIAPROCESSOR_AUDIO_TYPE_DTS;

                
                info->avinfo->audinfo[info->avinfo->audio_channel_num].valid = 1;
                info->avinfo->audinfo[info->avinfo->audio_channel_num].audio_pid = stream_pid;
                ts_iparse_get_codec_string(info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id, info->avinfo->audinfo[info->avinfo->audio_channel_num].desc);

    			descriptor_ptr = pmtPtr+5;
                es_info_length = ((pmtPtr[3]&0x0F)<<8) | (pmtPtr[4]) ;
                while(descriptor_ptr < pmtPtr+5+es_info_length)
                {
                    descriptor_tag = descriptor_ptr[0];
    				if(descriptor_tag == 0xa)
                    {
    					char lang[4] = {0};
    					int idx = 0;
                        
    					memcpy(lang, &descriptor_ptr[2], 3);					
    					idx = ts_iparse_get_langcode(lang);
    					if(idx >= 0)
    					{
    						find_lang = 1;
    						memcpy(info->avinfo->audinfo[info->avinfo->audio_channel_num].lang, s_fullcode[idx], strlen(s_fullcode[idx])>32?32:strlen(s_fullcode[idx]));
    					}
    					
    				}
                    descriptor_ptr += (2+descriptor_ptr[1]);      
                }
    			if(find_lang==0)
    			{
    				memcpy(info->avinfo->audinfo[info->avinfo->audio_channel_num].lang, "und", 3);
    			}
              
                info->avinfo->audio_channel_num++;
            }
		}
        else if(pmtPtr[0] == 0x06)
        {
            /*0x06保留字段*/
            
			stream_pid = ((pmtPtr[1] & 0x1f) << 8) | (pmtPtr[2] & 0xff);		
			stream_type = pmtPtr[0];
            
            descriptor_ptr = pmtPtr+5;
            
            info->avinfo->subtinfo[info->avinfo->subt_channel_num].subt_pid = stream_pid;
			info->avinfo->subtinfo[info->avinfo->subt_channel_num].subt_type = 0x108;//SUBT_DVB;
            memcpy(info->avinfo->subtinfo[info->avinfo->subt_channel_num].desc, "dvb", strlen("dvb"));
            
            es_info_length = ((pmtPtr[3]&0x0F)<<8) | (pmtPtr[4]) ;
            while(descriptor_ptr < pmtPtr+5+es_info_length)
            {
                descriptor_tag = descriptor_ptr[0];
                if(descriptor_tag == 0x6A)
                {
                    stream_pid = ((pmtPtr[1] & 0x1f) << 8) | (pmtPtr[2] & 0xff);		
			        stream_type = pmtPtr[0];
                    info->avinfo->audinfo[info->avinfo->audio_channel_num].audio_pid = stream_pid;
				    info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id = MEDIAPROCESSOR_AUDIO_TYPE_AC3_2;
                    info->avinfo->audinfo[info->avinfo->audio_channel_num].valid = 1;
			        ts_iparse_get_codec_string(info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id, info->avinfo->audinfo[info->avinfo->audio_channel_num].desc);
			        info->avinfo->audio_channel_num++;
                }
				else if(descriptor_tag == 0x7A)
                {
                    stream_pid = ((pmtPtr[1] & 0x1f) << 8) | (pmtPtr[2] & 0xff);		
			        stream_type = pmtPtr[0];
                    info->avinfo->audinfo[info->avinfo->audio_channel_num].audio_pid = stream_pid;
				    info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id = MEDIAPROCESSOR_AUDIO_TYPE_AC3_2;
                    info->avinfo->audinfo[info->avinfo->audio_channel_num].valid = 1;
			        ts_iparse_get_codec_string(info->avinfo->audinfo[info->avinfo->audio_channel_num].codec_id, info->avinfo->audinfo[info->avinfo->audio_channel_num].desc);
			        info->avinfo->audio_channel_num++;
                }			
				else if(descriptor_tag == 0xa)
                {
					char lang[4]={0};
                    int idx=0;
                    
					memcpy(lang, &descriptor_ptr[2], 3);
                    idx = ts_iparse_get_langcode(lang);
                    if(idx >= 0)
                    {
					    memcpy(info->avinfo->audinfo[info->avinfo->audio_channel_num].lang, s_fullcode[idx], strlen(s_fullcode[idx])>32?32:strlen(s_fullcode[idx]));
                    }
                    else
                    {
                        memcpy(info->avinfo->audinfo[info->avinfo->audio_channel_num].lang, "und",3);
                    }
                }
                else if(descriptor_tag == 0x59)//Subtitling descriptor来自en300468标准
                {
					char lang[4] = {0};
					int idx = 0;
                    
					memcpy(lang, &descriptor_ptr[2], 3);					
					idx = ts_iparse_get_langcode(lang);
					if(idx >= 0)
					{
	                    memcpy(info->avinfo->subtinfo[info->avinfo->subt_channel_num].lang, s_fullcode[idx], strlen(s_fullcode[idx])>32?32:strlen(s_fullcode[idx]));
					}
                    else
                    {
        				memcpy(info->avinfo->subtinfo[info->avinfo->subt_channel_num].lang, "und",3);                        
                    }
                    info->avinfo->subt_channel_num++;
                }
                descriptor_ptr += (2 + descriptor_ptr[1]);      
            }	    
        }            
        else 
        {
			//EIS_ASSERT(1);
			INFO("%s[ts_iparse_pmt_info]unknown stream type %d.\n",TS_PARSE_MARK,pmtPtr[0]);
		}

		es_info_length = ((pmtPtr[3]&0x0F)<<8) | (pmtPtr[4]) ;
		pmtPtr += (5+es_info_length);
	}

    if(info->avinfo->audio_channel_num > 0)
    {
        int i=0;
        for(i=0;i<info->avinfo->audio_channel_num;i++)
        {
            INFO("%s[ts_iparse_pmt_info]i= %d .audio pid:%d.aud stream type:%d\n",TS_PARSE_MARK,i,
                info->avinfo->audinfo[i].audio_pid,info->avinfo->audinfo[i].codec_id);
        }
        info->avinfo->audio_current_channel = 0;
    }

    if(info->avinfo->subt_channel_num > 0)
    {
        int i = 0;
        for(i = 0; i < info->avinfo->subt_channel_num; i ++)
        {
             INFO("%s[ts_iparse_pmt_info]i= %d, subt_pid = %d, subt_desc = %s\n",TS_PARSE_MARK,
                i, info->avinfo->subtinfo[i].subt_pid, info->avinfo->subtinfo[i].desc);          
        }
        info->avinfo->subt_current_channel = -1;        
    }    
        

	if(info->avinfo->audinfo[0].audio_pid == info->avinfo->vidinfo[0].video_pid)
		return IPANEL_ERR;
	else
		return IPANEL_OK;
}

/*获取音频压缩格式*/
static int ts_iparse_get_codec_string(int codec, unsigned char *buf)
{
    switch(codec)
    {
    	case MEDIAPROCESSOR_AUDIO_TYPE_MPEG1:
            memcpy(buf,"MPEG1",sizeof("MPEG1"));
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_MPEG2:
            memcpy(buf,"MPEG2",sizeof("MPEG2"));
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_MP3:
            memcpy(buf,"MP3",sizeof("MP3"));
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_AAC_1:
    	case MEDIAPROCESSOR_AUDIO_TYPE_AAC_2:
    	case MEDIAPROCESSOR_AUDIO_TYPE_AAC_3:
            memcpy(buf,"AAC",sizeof("AAC"));
            break;
    	case MEDIAPROCESSOR_AUDIO_TYPE_AC3_1:
    	case MEDIAPROCESSOR_AUDIO_TYPE_AC3_2:
    	case MEDIAPROCESSOR_AUDIO_TYPE_AC3_3:
            memcpy(buf,"AC3",sizeof("AC3"));
            break;
        default:
            memcpy(buf,"UND",sizeof("UND"));
            break;
    }
    return IPANEL_OK;
}

/*获取语言类型*/
static int ts_iparse_get_langcode(char *letter)
{
	int idx = -1;
	int i = 0;
    
	for(i = 0; i < sizeof(s_lettercode)/4; i++)
	{
		if(0 == strncmp(letter, s_lettercode[i], 3))
		{
			idx = i;
			break;
		}
	}
	if(idx == -1)
	{
		INFO("%s[ts_iparse_get_langcode] search lang code failed\n",TS_PARSE_MARK);
	}
	else
	{
		INFO("%s[ts_iparse_get_langcode] lang code %s %s\n", TS_PARSE_MARK, s_lettercode[idx], s_fullcode[idx]);
	}
	
	return idx;
}



/*解析ts流PTS信息*/
int ts_iparse_stream_pts(ts_stream_info *info,unsigned char *buffer,int len)
{
    unsigned char pes_buffer[2048] = {0};
    int i = 0,pes_buffer_len = 0,ts_pid = 0x1FFF;
    int ret = IPANEL_ERR,size = 0;
    int	adaptation_field_control = 0;
    int payload_unit_start_indicator = 0;
    int PTS_DTS_flags = 0;
    unsigned int pts = 0;
    unsigned char *tsp = buffer,*tsload = NULL,*p = NULL;

	
	FAILED_RETURNX( !info || !buffer || len < 188,IPANEL_ERR);
	
	while ( (0x47 != *tsp || tsp[188] != 0x47) && len >= 0)   
	{
		tsp++;
		len--;
	}
	
	for (i=0; i < len/188; i++)
	{
        // syn_byte: MPEG-2TS ( 0x47 )              
		if (0x47 != tsp[0])   
		{
			buffer +=188;
			continue;
		}

		ts_pid = (tsp[1]&0x1f)<<8 | tsp[2];
        
		//过滤掉其他TS包
		if( info->avinfo->vidinfo->video_pid != ts_pid)
		{
			tsp += 188;
			continue;
        }
		
		adaptation_field_control = (tsp[3]>>4)&0x3;
		payload_unit_start_indicator = (tsp[1]>>6) & 0x1;

		//发现新的PES开始
		if( payload_unit_start_indicator) 
        {
            //清空pes buffer
			pes_buffer_len = 0;

    		//剥出PES数据
    		tsload = NULL;
    		switch( adaptation_field_control )
    		{
    			case 0://保留
    			case 2://无净荷
    				break;			
    			case 1://无调整字段
    				tsload = tsp + 4;
    				break;
    			case 3://调整字段后是净荷
    				tsload = tsp + 5 + tsp[4];
    				break;
    		}
            
    		//保存PES的头部字节
    		if(tsload)
    		{
    			size = tsp + 188 - tsload;
    			if( sizeof(pes_buffer) < pes_buffer_len + size )
    				size = sizeof(pes_buffer) - pes_buffer_len;
                
    			if( 0 < size ) 
                {
    				memcpy(pes_buffer + pes_buffer_len, tsload, size);
    				pes_buffer_len += size;
    			}
    		}    

            
			if(pes_buffer_len > 9) 
			{
				p = pes_buffer;
				//packet_start_code_prefix(24) + streaid(8) + PES_packet_length(16)
				p += 6;
				//'10' + PES_scrambling_control(2) + PES_priority(1) + data_alignment_indicator(1) + copyright(1) + original_or_copy(1)
				p++;
				
				//PTS_DTS_flags(2) + ESCR_flag(1) + ES_rate_flag(1) + DSM_trick_mode_flag(1) + additional_copy_info_flag(1) + PES_CRC_flag(1) + PES_extension_flag(1)
				PTS_DTS_flags = (*p>>6)&0x3;
				p++;
				
				//PES_header_data_length(8)
				p++;
				
				if( PTS_DTS_flags == 0x2 ) 
                {
					pts = ts_iparse_getTimeStamp(p);
				} 
                else if( PTS_DTS_flags == 0x3 ) 
    			{
					pts = ts_iparse_getTimeStamp(p);                      						
				} 
                else if( PTS_DTS_flags != 0 ) 
                {
				    ;
				}
				
			}


			if( pts != 0 ) 
            {
				if( info->base_pts == 0) 
					info->base_pts = pts;
                info->current_pts = pts;
                
    			INFO("%s[ts_iparse_stream_pts]current_pts=%u base_pts=%u.\n",TS_PARSE_MARK, info->current_pts, info->base_pts);
                
				return IPANEL_OK;
			}
		} 
		
		tsp += 188;
	}
	
	return ret;
       
}


static unsigned int ts_iparse_getTimeStamp(unsigned char *buffer)
{
    unsigned int timeStamp = 0;
    unsigned char *p = buffer;

    if(NULL == p)
        return 0;

    timeStamp = ((*p>>1) & 0x7) << 29;
    p++;
    timeStamp += (*p) << 21;
    p++;
    timeStamp += ((*p)>>1) << 14;
    p++;
    timeStamp += (*p) << 6;
    p++;
    timeStamp += (*p) >> 2;
    p++;
    
    return (unsigned int) (timeStamp / 45);    
}


/*解析ts流视频的宽和高*/
int ts_iparse_stream_video_picture(ts_stream_info *info,unsigned char *buffer,int len)
{
    int find_system_header = 0;
    int pic_width = 0;
    int pic_height = 0;
    unsigned char *ptr = NULL;
    
	FAILED_RETURNX( !info || !buffer || len < 188,IPANEL_ERR);

    if(info->avinfo->vidinfo->codec_id == MEDIAPROCESSOR_VIDEO_TYPE_MPEG1
        || info->avinfo->vidinfo->codec_id == MEDIAPROCESSOR_VIDEO_TYPE_MPEG2)
    {
        ptr = buffer;
        while(ptr + 7 < buffer + len)
        {
            if(ptr[0] == 0x00 && ptr[1] == 0x00 && ptr[2] == 0x01 && ptr[3] == 0xb3)
            {
                find_system_header = 1;
                break;
            }
            ptr++;
        }
        
        if(find_system_header == 1)
        {
            pic_width  = ((ptr[4]<<8)|ptr[5])>>4;
            pic_height = ((ptr[5]&0x0f)<<8)|ptr[6];

            info->avinfo->vidinfo->width = pic_width;
            info->avinfo->vidinfo->height = pic_height;
                
    		INFO("%s[ts_iparse_stream_video_picture]width=%d height=%d.\n",TS_PARSE_MARK, pic_width, pic_height);
            
            return IPANEL_OK;
        } 
    }
    else if(info->avinfo->vidinfo->codec_id == MEDIAPROCESSOR_VIDEO_TYPE_H264)
    {
        return IPANEL_OK;
    }

    return IPANEL_ERR;
}



