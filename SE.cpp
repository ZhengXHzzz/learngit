#include<stdio.h>
#include<stdlib.h>
#include<time.h>

int main()
{
	int a[20];
	for(int i=0;i<50;i++)
	{
	  	a[i]=(rand()%(25-1)+1);
	  	printf("% d",a[i]);
	}
	printf("\n");
	
  return 0;
}
