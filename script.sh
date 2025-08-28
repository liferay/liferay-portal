
				
					#!/bin/bash

					until (: </dev/tcp/127.0.0.1/4444) &> /dev/null && (: </dev/tcp/127.0.0.1/22) &> /dev/null

					do
						echo "Waiting for Chrome 138.0 to be ready"

						sleep 10
					done
				
			